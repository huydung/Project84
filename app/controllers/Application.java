package controllers;

import play.*;
import play.data.validation.Required;
import play.data.validation.Validation;
import play.i18n.Lang;
import play.i18n.Messages;
import play.libs.WS;
import play.libs.XPath;
import play.mvc.*;

import java.text.Collator;
import java.util.*;

import org.w3c.dom.*;

import com.google.gson.JsonElement;
import com.sun.org.apache.xpath.internal.res.XPATHErrorResources;

import models.*;
import models.enums.ApprovalStatus;

public class Application extends Controller {
	
	@Before(unless={"homepage","rpx","switchLanguage","inviteResponseFromEmail"})
    static void setConnectedUser() {
        if(Security.isLoggedIn()) {
            User user = User.findById(Long.parseLong(Security.getConnectedUserId()));
            //If the cookie exist but user has been deleted from database
            if(user == null){
            	Security.logout();
            	homepage();
            }
            renderArgs.put("loggedin", user);
        }else{
        	Logger.debug("User is connected");
        	Application.homepage();
        }
    }
	
	//@Get("/home")
    public static void homepage() {
    	if( Security.isLoggedIn() ){
    		Application.app();
    	}
        render();
    }
    
    //@Get("/languages")
    public static void switchLanguage(String code, String destination){
    	if( code.equals("en") || code.equals("vi") ){
    		Lang.change(code);
    	}
    	redirect(destination);
    }
    
    //@Get("/")
    public static void app(){
    	User user = renderArgs.get("loggedin", User.class);
    	
    	//Check if there're invitations for this user
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
    		render("memberships/invitations.html", invitations, accept_id);
    	}else{
    		List<Membership> memberships = Membership.findByUser(user);
        	if( memberships == null || memberships.size() == 0 ){
        		Projects.create();
        	}else{
        		Projects.overview();
        	}
    	}
    }
    
    //@Post("/rpx")
    public static void rpx(){    	
    	try{
    		String token = params.get("token");
	    	User user = User.getFromRpx(token);
	    	if(user != null){
	    		Security.setConnectedUser(user.id);
	    		
	    		if( user.hasProfile ){
	    			Application.app();
	    		}else{
	    			flash.put("success", Messages.get("success.oauth", user.provider));
		    		flash.keep();
		    		Users.profile(user.id);	    			
	    		}
	    	}else{
	    		flash.put("error", Messages.get("error.oauth"));
	    		flash.keep();
	    		Application.homepage();
	    	}
    	}catch(Exception e){
    		renderText("Error get JSON response: "+ e.getMessage());
    	}
    }
    
    public static void inviteResponseFromEmail(
			@Required Long id, @Required String reply,
			@Required String inviteKey		
		){
    	if(Validation.hasErrors()){
    		AppController.displayValidationMessage();
    	}else{
			Membership m = Membership.findById(id);
			if( m == null ){ 
				AppController.notFound("Membership", id); 
			}else {
				
				if( !m.inviteKey.equals(inviteKey)){
					flash.put("error", Messages.get("invitation.wrongKey"));
				} else {
					if( reply.equals("accept") ){
						session.put("invitationId", id);
						flash.put("info", Messages.get("invitation.pleaseLogin"));
					} else {
						//user has denied to join
						m.deny();
						flash.put("success", Messages.get("invitation.denied", 
								m.project.creator.fullName,
								m.project.name));						
					}
				}							
			}
			Application.homepage();
    	}
	}
}