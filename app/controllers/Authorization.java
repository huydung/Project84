package controllers;

import models.Membership;
import models.Project;
import models.enums.PermissionKey;

import org.apache.commons.lang.StringUtils;

import play.mvc.Before;
import play.mvc.Controller;

public class Authorization extends Controller {
	@Before(priority=10)
	public static void checkAuthorization(){
		String method = request.actionMethod;
		Membership m = AppController.getActiveMembership();
		Project p = AppController.getActiveProject();
		//Projects Controller
		if( request.controllerClass == Projects.class ){			
			if( "create,doCreate,dashboard".contains(method) ){				
				if(m == null){	error(403, "Access Denied");}
			}
		}
		//Memberships Controller
		else if( request.controllerClass == Memberships.class ){
			if( "create,doCreate".contains(method) ){
				if( !p.allow(m, PermissionKey.CREATE_INVITATIONS)){  
					error(403, "Access Denied");	
				};	
			}else if( "edit,doEdit".contains(method) ){
				if( !p.allow(m, PermissionKey.EDIT_MEMBERSHIPS) ){
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
