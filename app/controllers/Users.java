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
	
	//@Get("/users/profile")
	public static void profile(Long id){
		User user = null;
		user = User.findById(id);
		
		if( user == null ){
			notFound("Profile", id);
		}	
		render(user);
	}
	
	//@Post("/users/profile")
	public static void saveProfile(@Valid User user){
		if( Validation.hasErrors() ){			
			displayValidationMessage();
			render("users/profile.html", user);
		}else{
			user.save();
			flash.put("success", Messages.get("success.profileSaved", user.nickName));
			flash.keep();
			Application.app();
		}
	}
}
