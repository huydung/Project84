package controllers;

import models.User;
import play.Logger;
import play.mvc.Before;
import play.mvc.Controller;
import play.mvc.Router;
import play.mvc.With;


public class AppController extends Controller {
	@Before
    static void setConnectedUser() {
        if(Security.isLoggedIn()) {
            User user = User.findById(Long.parseLong(Security.getConnectedUserId()));
            renderArgs.put("user", user);
        }else{
        	Logger.debug("User is connected");
        	Application.homepage();
        }
    }
	
}
