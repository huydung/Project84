package controllers;

import java.util.ArrayList;

import com.huydung.helpers.DateFormatOption;
import com.huydung.helpers.TimeZoneOption;
import com.huydung.utils.SelectorUtil;

import models.User;
import play.data.validation.Valid;
import play.data.validation.Validation;
import play.i18n.Messages;
import play.modules.router.Get;
import play.modules.router.Post;
import play.mvc.*;

public class Users extends AppController{
	
	//@Get("/users/profile")
	public static void profile(Long uid){
		User user = null;
		user = User.findById(uid);
		
		if( user == null ){
			error(404, Messages.get("error.notFound", "User", uid));
		}	
		render(user);
	}
	
	//@Post("/users/profile")
	public static void saveProfile(@Valid User user){
		if( Validation.hasErrors() ){			
			validationMessage();
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
