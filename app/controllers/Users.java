package controllers;

import java.util.ArrayList;

import com.huydung.helpers.DateFormatOption;
import com.huydung.helpers.TimeZoneOption;
import com.huydung.utils.SelectorUtil;

import models.User;
import play.data.validation.Valid;
import play.data.validation.Validation;
import play.i18n.Messages;
import play.mvc.*;

public class Users extends AppController{
	
	public static void profile(Long uid){
		User user = null;
		user = User.findById(uid);
		
		if( user == null ){
			flash.put("error", Messages.get("errors.notFound", "User", uid));
		}	
		render(user);
	}
	
	public static void saveProfile(@Valid User user){
		if( Validation.hasErrors() ){			
			flash.put("error", Messages.get("error.validation"));
			render("users/profile.html", user);
		}else{
			user.save();
			flash.clear();
			flash.put("success", Messages.get("success.profileSaved", user.nickName));
			flash.keep();
			Application.app();
		}
	}
}
