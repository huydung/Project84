package models;

import play.*;
import play.data.validation.MaxSize;
import play.data.validation.Required;
import play.db.jpa.*;
import play.mvc.Router;

import javax.persistence.*;

import models.enums.ActivityAction;

import org.hibernate.Filter;
import org.hibernate.Session;

import controllers.AppController;

import java.util.*;

@Entity

public class Comment extends BasicItem implements IActivityLoggabe {
	
	@Lob
	@Required
	@MaxSize(4000)
	public String body;
	
	@Required
	@ManyToOne
	public Item parent;
	
	@Required
	@ManyToOne
	public Project project;
	
	public Comment() {
		super();
	}	
	
	public static long countCommentsOfItem(Item item){
		return Comment.count( "parent = ? ", item );
	}
     
	public static List<Comment> getCommentsOfItem(Item item){
		return Comment.find( "parent = ? ", item ).fetch();
	}
	
	public static List<Comment> findSinceDate(Date d, Project p){
		return Comment.find("project = ? AND created >= ?", p, d).fetch();
	}
	
	public void beforeSave(){
		super.beforeSave();
		this.project = this.parent.project;
	}
	
	@Override
	public boolean create(){
		boolean res = super.create();
		if(res){
			Activity.track(this, this.creator, ActivityAction.CREATE);
		}
		return res;
	}
	
	public Comment delete(User user){
		this.deleted = true;
		this.save();
		Activity.track(this, user, ActivityAction.DELETE);
		return this;
	}
	
	@Override
	public Project getProject() {
		return project;
	}

	@Override
	public String getLogName() {
		return name;
	}

	@Override
	public String getActivityShowLink() {
		Map<String, Object> args = new HashMap<String, Object>();
		args.put("item_id",	this.parent.id);
		String url = Router.getFullUrl("Items.show", args);
		url += "#comment-" + this.id;
		return url;
	}
}
