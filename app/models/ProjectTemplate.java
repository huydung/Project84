package models;

import play.*;
import play.data.validation.Check;
import play.data.validation.CheckWith;
import play.data.validation.MaxSize;
import play.data.validation.Required;
import play.db.jpa.*;

import javax.persistence.*;
import java.util.*;

@Entity
public class ProjectTemplate extends Model {
	@Required
	public String name;

	@Required
	public Boolean isSystem;
	
	@Required
	public String description;

	@Required
	@MaxSize(20000)
	@Lob
	public String content;

	@ManyToOne
	@CheckWith(MustHaveUserIfNotSystem.class)
	public User user;
	
	@OneToMany
	public List<Project> projects;

	public ProjectTemplate(String name, Boolean isSystem) {
		super();
		this.name = name;
		this.isSystem = isSystem;
	}

	static class MustHaveUserIfNotSystem extends Check {
		public boolean isSatisfied(Object template, Object user) {
    	  ProjectTemplate pt = (ProjectTemplate)template;
    	  if( !pt.isSystem && user == null ){
    		  return false;
    	  }
    	  return true;
		}	
	}
	
	@Override
	public String toString(){
		return (isSystem ? "[System] " : "") + name;
	}
	
	public static List<ProjectTemplate> getTemplates(User user){
		return ProjectTemplate.find(
				"isSystem = ? OR (isSystem = ? AND user = ?)", true, false, user).fetch();
	}
}
