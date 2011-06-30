package models;

import play.*;
import play.data.validation.MaxSize;
import play.data.validation.Required;
import play.db.jpa.*;

import javax.persistence.*;

import org.hibernate.Filter;
import org.hibernate.Session;
import java.util.*;

@Entity

public class Comment extends BasicItem {
	
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
	
	public void beforeSave(){
		super.beforeSave();
		this.project = this.parent.project;
	}
	/*
	public static List<Comment> findDeleted(){
		Filter deleteFilter = ((Session)JPA.em().getDelegate()).getEnabledFilter("deleted");
		deleteFilter.setParameter("deleted", true);
		List<Comment> comments = Comment.findAll();
		deleteFilter.setParameter("deleted", false);
		return comments;
	}
	*/
}
