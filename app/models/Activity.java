package models;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

import com.huydung.helpers.ActionResult;

import models.enums.ActivityType;

import play.data.validation.Required;
import play.db.jpa.Model;
import play.i18n.Messages;

@Entity
public class Activity extends Model {
	
	@Required
	public String message;
	
	@Required
	public Date created = new Date();
	
	@ManyToOne
	public User creator;
	
	@Required
	@ManyToOne
	public Project project;
	
	@Required
	public String type;
	
	public static ActionResult track( String message, Project project, ActivityType type, User user ){
		Activity activity = new Activity();
		activity.setActivityType(type);
		activity.message = message;
		activity.project = project;
		activity.creator = user;
		activity.created = new Date();
		if( activity.validateAndSave() && activity.id > 0 ){
			return new ActionResult(true);
		}else{
			return new ActionResult(false, 
				Messages.get("warning.activity.save"));
		}
	}
	
	public void setActivityType(ActivityType t){
		type = t.getName();
	}
	
	public ActivityType getActivityType(){
		return ActivityType.parse(type);
	}
}
