package controllers;

import java.util.TimeZone;


import models.User;
import play.Logger;
import play.Play;
import play.data.binding.types.DateBinder;
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
        }else{
        	Application.homepage();
        }
    }
	
	protected static User getLoggedin(){
		return renderArgs.get("loggedin", User.class);
	}

	protected static void displayWarning(String message, String action){
		flash.put("warning", 
				message + "<br/>" +
				Messages.get("actions.contactWarning", action));	
	}
	
	protected static void displayError(String message, String action){
		flash.put("error", 
				message + "<br/>" +
				Messages.get("actions.contactError", action));
		
	}
	
	protected static void validationMessage(){
		flash.put("error", Messages.get("error.validation") );
	}
}
