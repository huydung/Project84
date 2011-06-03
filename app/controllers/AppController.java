package controllers;

import java.util.TimeZone;

import org.hibernate.Session;


import models.Membership;
import models.Project;
import models.User;
import play.Logger;
import play.Play;
import play.data.binding.types.DateBinder;
import play.db.jpa.JPA;
import play.i18n.Messages;
import play.mvc.Before;
import play.mvc.Controller;
import play.mvc.Router;
import play.mvc.With;


public class AppController extends Controller {
	
	@Before
    static void setConnectedUser() {
        if(Security.isLoggedIn()) {
            User user = User.findById(Long.parseLong(Security.getConnectedUserId()));
           //If the cookie exist but user has been deleted from database
            if(user == null){
            	Security.logout();
            	Application.homepage();
            }
            Play.configuration.setProperty("date.format", user.dateFormat);
            TimeZone.setDefault(TimeZone.getTimeZone(user.timeZone));
            renderArgs.put("loggedin", user);
            
            //get active project, if having it
            String project_id = params.get("project_id");
    		if( project_id != null ){
    			Project project = Project.findById(Long.parseLong(project_id));
    			
    			if( project != null ){
    				renderArgs.put("_project", project);
    				System.out.println(request.url + ": Saved project to renderArgs (" + project.name + ")");
    				Membership m = Membership.findByProjectAndUser(project, user);
    				renderArgs.put("_membership", m);
    			}
    		}
        }else{
        	Application.homepage();
        }
    }
	
	@Before
	static void setFilters(){
		((Session)JPA.em().getDelegate())
			.enableFilter("deleted")
			.setParameter("deleted", false);
	}
	
	@Before
	static void checkAjax(){
		renderArgs.put("ajax", request.isAjax());
	}

	
	static User getLoggedin(){
		return renderArgs.get("loggedin", User.class);
	}
	
	static Project getActiveProject(){
		return renderArgs.get("_project", Project.class);
	}
	
	static Membership getActiveMembership(){
		try{
			return renderArgs.get("_membership", Membership.class);
		} catch(Exception e){
			return null;
		}
	}

	static void displayWarning(String message, String action){
		flash.put("warning", 
				message + "<br/>" 
				//+	Messages.get("actions.contactWarning", action)
				);	
	}
	
	static void displayError(String message, String action){
		flash.put("error", 
				message + "<br/>" 
				//+ Messages.get("actions.contactError", action)
				);
		
	}
	
	static void displayValidationMessage(){
		flash.put("error", Messages.get("error.validation") );
	}
	
	static void notFound(String object, Long id){
		error(404, Messages.get("error.notFound", object, id));		
	}
}
