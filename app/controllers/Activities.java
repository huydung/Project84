package controllers;

import java.util.Date;
import java.util.List;

import models.Activity;
import models.Project;
import models.User;

import play.data.validation.Required;
import play.data.validation.Validation;
import play.mvc.Controller;

public class Activities extends AppController {
	public static void dashboard(@Required Long project_id){
		if( Validation.hasErrors() ){
			error(400, "Bad Request");
		}
		User user = getLoggedin();
		Project p = getActiveProject();
		Date lastLoggedIn = Security.getLastLoggedIn();
		List<Activity> recent_activities = Activity.findSinceDate(lastLoggedIn, p);
		
		List<Date> dates = null;
		if( recent_activities.size() < Activity.count("project = ?", p) ){
			dates = Activity.findAvailableDateBefore(user.lastLoggedIn, p);
		}
		
		render(recent_activities, dates);
	}
}
