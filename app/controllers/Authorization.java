package controllers;

import java.util.HashMap;

import models.Comment;
import models.Item;
import models.Listing;
import models.Membership;
import models.Project;
import models.User;
import models.enums.PermissionKey;

import org.apache.commons.lang.StringUtils;

import com.huydung.utils.MiscUtil;

import play.mvc.Before;
import play.mvc.Controller;
import play.mvc.Finally;
import play.mvc.Util;
import play.templates.JavaExtensions;
import sun.security.action.GetLongAction;

public class Authorization extends Controller {
	
	private static HashMap<String, Boolean> perms = new HashMap<String, Boolean>();

	@Finally
	public static void clearPermissionsCacheIfNeeded(){
		if( 
			(request.controllerClass == Memberships.class && "doEdit,delete".contains(request.actionMethod)) ||
			(request.controllerClass == Projects.class && "savePermissions".contains(request.actionMethod))
		){
			MiscUtil.ConsoleLog("Clear Cache because may be there're changes in memberships permission");
			perms.clear();
		}
	}
	
	public static boolean check( String cacheKey, Long project_id, Long user_id, String permKey){
		if( perms.containsKey(cacheKey) ){
			MiscUtil.ConsoleLog("Get from permissions cache: " + cacheKey );
			return perms.get(cacheKey);
		}else{
			Project p = AppController.getActiveProject();
			if(p == null || ( p != null && p.id != project_id )){
				 MiscUtil.ConsoleLog("Need to fetch Project from db");
				 p = Project.findById(project_id);
				 if(p==null){return false;}
			}
			
			Membership m = AppController.getActiveMembership();
			if(m == null || ( m != null && m.user.id != user_id )){
				 MiscUtil.ConsoleLog("Need to fetch Membership from db");
				 m = Membership.findByProjectAndUser(p, (User)User.findById(user_id));				 
			}
			
			boolean isAllow = p.allow(m, permKey);
			perms.put(cacheKey, isAllow);
			MiscUtil.ConsoleLog("Added to permissions cache: " + cacheKey + " - " + isAllow);
			return isAllow;
		}
	}
	
	public static boolean check( Long project_id, Long user_id, PermissionKey key, Listing l){
		if( project_id == null || user_id == null || l == null){
			return false;
		}
		String cacheKey = project_id + ":" + user_id + ":" + key + ":" + l.id;
		return check(cacheKey, project_id, user_id, key.toString() + "_" + l.id);		
	}
	
	public static boolean check( Long project_id, Long user_id, PermissionKey key){
		if( project_id == null || user_id == null){
			return false;
		}
		String cacheKey = project_id + ":" + user_id + ":" + key;
		return check(cacheKey, project_id, user_id, key.toString());
	}
	
	@Before(priority=10)
	public static void checkAuthorization(){
		String method = request.actionMethod;
		Membership m = AppController.getActiveMembership();
		Project p = AppController.getActiveProject();
		Listing l =  AppController.getListing();
		Item item = AppController.getItem();
		if( request.controllerClass == Projects.class ){			
			checkProjectsController(method, m, p);
		}
		else if( request.controllerClass == Memberships.class ){
			checkMembershipsController(method, m, p);
		}
		else if( request.controllerClass == Listings.class ){
			checkListingsController(method, m, p, l);	
		}
		else if( request.controllerClass == Items.class ){
			checkItemsController(method, m, p, l, item);
		}
		else if( request.controllerClass == Comments.class ){
			checkCommentsController(method, m, p, l);
		}
	}

	private static void checkProjectsController(String method, Membership m,
			Project p) {
		if( "dashboard".contains(method) ){				
			if(m == null){	
				error(403, "Access Denied");
			}
		}else if( "structure".contains(method) ){
			if( p == null ){error(403, "Access Denied");}
			if( !p.allow(m, PermissionKey.EDIT_PROJECT_INFO)){  
				error(403, "Access Denied");	
			};	
		}else if( "permissions,savePermissions".contains(method) ){
			if( p == null ){error(403, "Access Denied");}
		
			if( !p.allow(m, PermissionKey.EDIT_USERS_PERMISSIONS)){  
				error(403, "Access Denied");	
			};	
		}
	}

	private static void checkMembershipsController(String method, Membership m,
			Project p) {
		if( p == null ){error(403, "Access Denied");}
		if( "create,doCreate".contains(method) ){
			if( !p.allow(m, PermissionKey.CREATE_INVITATIONS)){  
				error(403, "Access Denied");	
			};	
		}else if( "edit,doEdit".contains(method) ){
			if(!p.allow(m, PermissionKey.EDIT_MEMBERSHIPS) ){
				if( params.get("id", Long.class) == m.id ){
					if( !p.allow(m, PermissionKey.EDIT_OWN_MEMBERSHIPS) ){
						error(403, "Access Denied");
					}
				}else{
					error(403, "Access Denied");
				}
			}
		}
	}

	private static void checkCommentsController(String method, Membership m,
			Project p, Listing l) {
		if( "doCreate".contains(method) ){
			if( !p.allow(m, PermissionKey.CREATE_COMMENT, l) ){
				error(403, "Access Denied");
			}
		}else if( "delete".contains(method) ){				
			if( !p.allow(m, PermissionKey.DELETE_COMMENT, l) ){
				Comment cm = Comment.findById(params.get("comment_id", Long.class));
				if( cm != null && cm.creator.id == m.user.id ){
					if( !p.allow(m, PermissionKey.DELETE_OWN_COMMENT, l) ){
						error(403, "Access Denied");
					}
				}else{error(403, "Access Denied");}
			}
		}
	}
	
	@Util
	private static void checkItemsController(String method, Membership m,
			Project p, Listing l, Item item) {
		if( "show".contains(method) ){
			if( !p.allow(m, PermissionKey.VIEW_ITEMS, l) ){
				error(403, "Access Denied");
			}
		}else if( "quickEdit,edit,doEdit".contains(method) ){				
			if( !p.allow(m, PermissionKey.EDIT_ITEM, l) ){
				if( item != null && item.creator.id == m.user.id ){
					if( !p.allow(m, PermissionKey.EDIT_OWN_ITEM, l) ){
						error(403, "Access Denied");
					}
				}else{
					error(403, "Access Denied");
				}					
			}
		}else if( "updateCheck".contains(method) ){
			if( !p.allow(m, PermissionKey.CHECK_ITEM, l) ){
				error(403, "Access Denied");
			}
		}else if( "create,doCreate,doCreateFromForm".contains(method) ){
			if( !p.allow(m, PermissionKey.CREATE_ITEM, l) ){
				error(403, "Access Denied");
			}
		}else if( "delete".contains(method) ){				
			if( !p.allow(m, PermissionKey.DELETE_ITEM, l) ){
				if( item != null && item.creator.id == m.user.id ){
					if( !p.allow(m, PermissionKey.DELETE_OWN_ITEM, l) ){
						error(403, "Access Denied");
					}
				}else{error(403, "Access Denied");}
			}
		}
	}
	
	@Util
	private static void checkListingsController(String method, Membership m, Project p, Listing l){
		if( "dashboard".contains(method) ){
			if( p == null || l == null || m == null){ error(403, "Access Denied"); };
			if( !p.allow(m, PermissionKey.VIEW_ITEMS, l) ){
				error(403, "Access Denied");
			}
		}			
		else if( "saveOrderings,doEdit,doCreate".contains(method) ){
			if( p == null || l == null || m == null){ error(403, "Access Denied"); };			
			if( !p.allow(m, PermissionKey.LISTING_CONFIG, l) ){
				error(403, "Access Denied");
			}
		}else if( "delete".contains(method) ){
			if( p == null || l == null || m == null){ error(403, "Access Denied"); };			
			if( !p.allow(m, PermissionKey.DELETE_LISTING, l) ){
				error(403, "Access Denied");
			}
		}
	}
}
