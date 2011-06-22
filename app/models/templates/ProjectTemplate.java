package models.templates;

import play.*;
import play.data.validation.Check;
import play.data.validation.CheckWith;
import play.data.validation.MaxSize;
import play.data.validation.Required;
import play.db.jpa.*;
import play.i18n.Messages;

import javax.persistence.*;

import models.Project;
import models.User;
import models.validators.MustHaveUserIfNotSystem;


import java.util.*;

@Entity
public class ProjectTemplate extends Template {	
	
	public ProjectTemplate(String name, Boolean isSystem, User user,
			Boolean needMembers) {
		super(name, isSystem, user);
		this.needMembers = needMembers;
	}
	
	@OneToMany(mappedBy = "projectTemplate")
	public List<ProjectListTemplate> projectList;
	
	public Boolean needMembers;
	
	@Override
	public String toString(){
		return (isSystem ? "[" +  Messages.get("labels.system") + "] " : "") + name;
	}
	
	public static List<ProjectTemplate> getTemplates(User user){
		return ProjectTemplate.find(
				"isSystem = ? OR (isSystem = ? AND user = ?)", true, false, user).fetch();
	}
	
	public void addList(ListTemplate lt, String name){
		ProjectListTemplate plt = ProjectListTemplate.findByProjectAndListTemplate(this, lt);
		if( plt != null ){
			plt.name = name;			
		}else{
			plt = new ProjectListTemplate(this, lt, name);
		}
		plt.save();
	}
	
	public List<ListTemplate> getListTemplates(){
		ArrayList<ListTemplate> lts = new ArrayList<ListTemplate>();
		if( this.projectList != null ){
			for( ProjectListTemplate plt : this.projectList ){
				if( !lts.contains(plt.listTemplate) ){
					plt.listTemplate.name = plt.name;					
					lts.add(plt.listTemplate);
				}
			}
		}
		return lts;
	}
}
