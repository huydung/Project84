package models;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;
import javax.persistence.Query;
import javax.persistence.Transient;

import org.apache.commons.lang.time.DateUtils;
import org.apache.poi.openxml4j.opc.TargetMode;

import com.huydung.helpers.ActionResult;
import com.huydung.utils.Link;

import models.enums.ActivityAction;

import play.data.binding.types.DateBinder;
import play.data.validation.Required;
import play.db.jpa.JPA;
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
	public String link;
	
	@Required
	@ManyToOne
	public Project project;
		
	@Enumerated(EnumType.STRING)
	@Required
	public ActivityAction action;
	
	public Activity(Project project){
		super();
		this.project = project;
	}

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
	
	public static List<Activity> findSinceDate(Date d, Project p){
		return Activity.find("project = ? AND created >= ?", p, d).fetch();
	}
	
	public static List<Date> findAvailableDateBefore(Date d, Project p){
		String sql = "SELECT DISTINCT(DATE_FORMAT(created, '%Y-%m-%d')) FROM Activity WHERE project = ? AND created < ?";
		Query query = JPA.em().createNativeQuery(sql);
		query.setParameter(1, p);
		query.setParameter(2, d);
		List results = query.getResultList();
		Iterator<Object> ite = results.iterator();
		List<Date> dates = new ArrayList<Date>();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		while(ite.hasNext()){			
			try {
				Date date = sdf.parse(ite.next().toString());
				dates.add(date);
			} catch (ParseException e) {
				//Do nothing
			}
			
		}
		return dates;
	}
	
	/** LOGGING UTILITY **/
	public static Activity track(IActivityLoggabe obj, User user, ActivityAction action){
		return track(obj, user, action, null);
	}
	
	public static Activity track(IActivityLoggabe obj, User user, ActivityAction action, String message){
		Activity a = new Activity(obj.getProject(), user, action, obj.getId());
		a.link = obj.getActivityShowLink();
		String nickName;
		if( user == null ){
			nickName = "not Loggedin";
		}else{
			nickName = user.nickName;
		}
		if( message == null ){
			a.message = buildMessage(a.project.lang, obj.getType(), obj.getLogName(), nickName, action.toString());
		}else{
			a.message = message;
		}
		a.save();
		return a;
	}

	protected static String buildMessage(
			String lang, String type, String name, String userName, String action
		){
		return "[" + type.toUpperCase() + ": " + name + "] " 
			+ Messages.getMessage(lang, "labels.haveBeen") + " " + Messages.getMessage(lang, "labels." + action.toLowerCase()) + " " + Messages.getMessage(lang, "labels.by")
			+ " [" + userName + "]";
	}
	
	@Override
	public String getSubInfo() {
		return JavaExtensions.since(this.created);
	}

	@Override
	public Link getInfo() {	
		return new Link(this.message, this.link);
	}

	@Override
	public String getWidgetName() {	
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
