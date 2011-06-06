package controllers;

import java.util.HashMap;

import models.Membership;
import models.Project;
import models.User;
import models.enums.PermissionKey;

import org.apache.commons.lang.StringUtils;

import com.huydung.utils.MiscUtil;

import play.mvc.Before;
import play.mvc.Controller;
import play.mvc.Finally;

public class Authorization extends Controller {
	
	private static HashMap<String, Boolean> perms = new HashMap<String, Boolean>();

	@Finally
	public static void clearPermissionsCacheIfNeeded(){
		if( request.controllerClass == Memberships.class && 
				"doEdit,delete".contains(request.actionMethod)
		){
			MiscUtil.ConsoleLog("Clear Cache because may be there're changes in memberships permission");
			perms.clear();
		}		 
	}
	
	public static boolean check( Long project_id, Long user_id, PermissionKey key){
		if( project_id == null || user_id == null){
			return false;
		}

		String cacheKey = project_id + ":" + user_id + ":" + key;
		//Check in cache first
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
			
			boolean isAllow = p.allow(m, key);
			perms.put(cacheKey, isAllow);
			MiscUtil.ConsoleLog("Added to permissions cache: " + cacheKey + " - " + isAllow);
			return isAllow;
		}
	}
	
	@Before(priority=10)
	public static void checkAuthorization(){
		String method = request.actionMethod;
		Membership m = AppController.getActiveMembership();
		Project p = AppController.getActiveProject();
		//Projects Controller
		if( request.controllerClass == Projects.class ){			
			if( "dashboard".contains(method) ){				
				if(m == null){	
					error(403, "Access Denied");
				}
			}
		}
		//Memberships Controller
		else if( request.controllerClass == Memberships.class ){
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
	}
}
