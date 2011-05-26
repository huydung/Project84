package controllers;

import play.*;
import play.i18n.Lang;
import play.i18n.Messages;
import play.libs.WS;
import play.libs.XPath;
import play.modules.router.Get;
import play.modules.router.Post;
import play.mvc.*;

import java.util.*;

import org.w3c.dom.*;

import com.google.gson.JsonElement;
import com.sun.org.apache.xpath.internal.res.XPATHErrorResources;

import models.*;

public class Application extends Controller {
	
	@Before(unless={"homepage","rpx","switchLanguage"})
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
    	List<Membership> memberships = Membership.findByUser(
    		renderArgs.get("loggedin", User.class)
    	);
    	if( memberships == null || memberships.size() == 0 ){
    		Projects.create();
    	}else{
    		Projects.overview();
    	}
    	//render();
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
    
}