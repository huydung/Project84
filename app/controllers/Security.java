package controllers;

import play.mvc.Controller;
import models.User;

public class Security extends Controller {
	
	static boolean isLoggedIn(){		
		return session.contains("identifier");
	}
	
	static String getConnectedUserId(){
		return session.get("identifier");
	}
	
	static void setConnectedUser(Long uid){		
		session.put("identifier", uid);
	}
	
	public static void logout(){
		session.remove("identifier");
		Application.homepage();
	}
}
