package controllers;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.huydung.utils.MiscUtil;

import play.mvc.Controller;
import sun.security.action.GetLongAction;
import models.User;

public class Security extends Controller {
	
	static boolean isLoggedIn(){		
		return session.contains("identifier");
	}
	
	static String getConnectedUserId(){
		return session.get("identifier");
	}
	
	static void setConnectedUser(User user){		
		session.put("lastLoggedIn", user.lastLoggedIn.getTime());
		MiscUtil.ConsoleLog("Last time you loggedin: " + user.lastLoggedIn);
		user.lastLoggedIn = new Date();
		user.save();
		session.put("identifier", user.id);
	}
	
	static Date getLastLoggedIn(){
		SimpleDateFormat sdf = new SimpleDateFormat();
		String lli = session.get("lastLoggedIn");
		if( lli != null ){
			return new Date(Long.parseLong(lli));
		}else{
			MiscUtil.ConsoleLog("Error when trying to get lastLoggedIn from session");
			return new Date();
		}
	}
	
	public static void logout(){
		session.remove("identifier");
		flash.keep();
		flash.put("info", "You've been logged out");
		Application.homepage();
	}
}
