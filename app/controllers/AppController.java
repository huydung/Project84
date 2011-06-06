package controllers;

import java.util.List;
import java.util.TimeZone;

import org.hibernate.Session;

import com.huydung.utils.MiscUtil;


import models.Membership;
import models.Project;
import models.User;
import models.enums.ApprovalStatus;
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
	
	@Before(priority=1)
    static void prepare() {
		//Check if user has been login. If she hasn't, redirect to homepage, 
        if(Security.isLoggedIn()) {
        	
        	//Fetch the user information from db and set up some configuration
        	//accroding to her choices
            User user = User.findById(Long.parseLong(Security.getConnectedUserId()));
            //If the cookie exist but user has been deleted from database
            if(user == null){
            	Security.logout();
            	Application.homepage();
            }
            Play.configuration.setProperty("date.format", user.dateFormat);
            TimeZone.setDefault(TimeZone.getTimeZone(user.timeZone));
            renderArgs.put("loggedin", user);
            
            //Check invitations
            if( !request.isAjax() && request.method.toLowerCase().equals("get") ){
            	List<Membership> invitations = Membership.findByUserEmailAndStatus(
            			user.email, ApprovalStatus.WAITING_INVITE);
            	Long accept_id = -1L;
            	if( session.contains("invitationId") ){
            		accept_id = Long.parseLong(session.get("invitationId"));
            		boolean existed = false;
            		for( Membership m : invitations ){
            			if( m.id == accept_id ){
            				existed = true; break;
            			}
            		}
            		if(!existed){
            			invitations.add((Membership)Membership.findById(accept_id));
            		}
            	}
            	
            	if(!invitations.isEmpty()){
            		flash.put("info", Messages.get("messages.invitations"));
            		render("memberships/invitations.html", invitations, accept_id);
            	}
            }
            
            //get active projects of the logged in user, if she having it
            String project_id = params.get("project_id");
    		if( project_id != null ){
    			Project project = Project.findById(Long.parseLong(project_id));
    			
    			if( project != null ){
    				renderArgs.put("_project", project);
    				MiscUtil.ConsoleLog(request.url + ": Saved project to renderArgs (" + project.name + ")");
    				Membership m = Membership.findByProjectAndUser(project, user);
    				renderArgs.put("_membership", m);
    			}
    		}
    		
    		//set up paramater to tell is the current request is sent by AJAX
    		renderArgs.put("ajax", request.isAjax());
    		
    		//set up Hibernate filter for soft-delete mechanism
    		//@see: models.package-info.java
    		((Session)JPA.em().getDelegate())
			.enableFilter("deleted")
			.setParameter("deleted", false);
        }else{
        	Application.homepage();
        }
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
