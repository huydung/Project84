package controllers;

import java.util.TimeZone;

import models.User;
import play.Logger;
import play.Play;
import play.mvc.Before;
import play.mvc.Controller;
import play.mvc.Router;
import play.mvc.With;


public class AppController extends Controller {
	@Before
    static void setConnectedUser() {
        if(Security.isLoggedIn()) {
            User user = User.findById(Long.parseLong(Security.getConnectedUserId()));
            renderArgs.put("loggedin", user);
            Play.configuration.setProperty("date.format", user.dateFormat);
            TimeZone.setDefault(TimeZone.getTimeZone(user.timeZone));
        }else{
        	Logger.debug("User is connected");
        	Application.homepage();
        }
    }
	
	protected static User getLoggedin(){
		return renderArgs.get("loggedin", User.class);
	}
	
}
