package controllers;

import java.util.ArrayList;

import com.huydung.helpers.DateFormatOption;
import com.huydung.helpers.TimeZoneOption;
import com.huydung.utils.SelectorUtil;

import models.User;
import play.i18n.Messages;
import play.mvc.*;

public class Users extends AppController{
	
	public static void profile(Long uid){
		User user = null;
		user = User.findById(uid);
		if( user == null ){
			flash.put("error", Messages.get("errors.notFound", "User", uid));
		}	
		ArrayList<TimeZoneOption> timezones = SelectorUtil.getTimezones();
		ArrayList<DateFormatOption> formats = SelectorUtil.getDateFormats();
		render(user, timezones, formats);
	}
	
	public static void saveProfile(){
		flash.put("info", "TODO: Code process to check and save the user");		
		render();
	}
}
