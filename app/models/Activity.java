package models;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;

import org.apache.poi.openxml4j.opc.TargetMode;

import com.huydung.helpers.ActionResult;
import com.huydung.utils.Link;

import models.enums.ActivityAction;

import play.data.validation.Required;
import play.db.jpa.Model;
import play.i18n.Lang;
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
	public Long targetId;
	
	@Required
	@ManyToOne
	public Project project;
		
	@Enumerated(EnumType.STRING)
	@Required
	public ActivityAction action;

	public Activity(Project project, User user, ActivityAction action, Long targetId){
		super();
		this.creator = user;
		this.project = project;
		this.action = action;
		this.targetId = targetId;
	}
	
	@PrePersist
	public void beforeSave(){
		this.created = new Date();
	};
	
	
	/** LOGGING UTILITY **/
	public static Activity track(IActivityLoggabe obj, User user, ActivityAction action){
		return track(obj, user, action, null);
	}
	
	public static Activity track(IActivityLoggabe obj, User user, ActivityAction action, String message){
		Activity a = new Activity(obj.getProject(), user, action, obj.getId());
		if( message == null ){
			a.message = buildMessage(a.project.lang, obj.getType(), obj.getName(), user.nickName, action.toString());
		}else{
			a.message = message;
		}
		a.save();
		return a;
	}

	protected static String buildMessage(
			String lang, String type, String name, String userName, String action
		){
		String userLang = null;
		if( !lang.equals(Lang.get()) ){
			userLang = Lang.get();
			Lang.set(lang);
		}
		String msg = "[" + type.toUpperCase() + ": " + name + "] " 
			+ Messages.get("labels.haveBeen") + " " + Messages.get("labels." + action.toLowerCase()) + " " + Messages.get("labels.by")
			+ "[" + userName + "]";
		if( userLang != null ){
			Lang.set(userLang);
		}
		return msg;
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
