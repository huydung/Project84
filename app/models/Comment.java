package models;

import play.*;
import play.data.validation.MaxSize;
import play.data.validation.Required;
import play.db.jpa.*;

import javax.persistence.*;

import org.hibernate.annotations.Filter;


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
}
