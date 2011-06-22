package models;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.ManyToOne;

import com.huydung.helpers.ActionResult;
import com.huydung.utils.Link;

import models.enums.ActivityType;

import play.data.validation.Required;
import play.db.jpa.Model;
import play.i18n.Messages;
import play.mvc.Router;
import play.templates.JavaExtensions;

@Entity
public class Activity extends Model implements IWidget, IWidgetItem {
	
	@Required
	public String message;
	
	@Required
	public Date created = new Date();
	
	@ManyToOne
	public User creator;
	
	@Required
	@ManyToOne
	public Project project;
		
	@Enumerated(EnumType.STRING)
	@Required
	public ActivityType type;
	
	public Activity(Project project){
		super();
		this.project = project;
	}
	
	public static ActionResult track( String message, Project project, ActivityType type, User user ){
		Activity activity = new Activity(project);
		activity.type = type;
		activity.message = message;		
		activity.creator = user;
		activity.created = new Date();
		if( activity.validateAndSave() && activity.id > 0 ){
			return new ActionResult(true);
		}else{
			return new ActionResult(false, 
				Messages.get("warning.activity.save"));
		}
	}
	
	@Override
	public String getSubInfo() {
		return JavaExtensions.since(this.created);
	}

	@Override
	public Link getInfo() {	
		return new Link(this.message, "#");
	}

	@Override
	public String getName() {		
		return Messages.get("labels.activity.recent");
	}

	@Override
	public String getSubName() {		
		return "thời điểm";
	}

	@Override
	public String getHtmlClass() {
		return "activities";
	}

	@Override
	public Link getFirstLink() {		
		Map<String, Object> args = new HashMap<String, Object>();
		args.put("project_id", this.project.id);
		return new Link(
			Messages.get("labels.viewAll"),
			Router.getFullUrl("Activities.dashboard", args),
			"icon-link-list"
		);
	}

	@Override
	public Link getLastLink() {
		return null;
	}

	@Override
	public List getItems() {		
		return Activity.find("project = ? ORDER BY created DESC", project).fetch(20);
	}

	@Override
	public String getHtml() {
		return null;
	}
	
	@Override
	public int getColSpan(){
		return 3;
	}
}
