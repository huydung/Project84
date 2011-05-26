package models.templates;

import play.*;
import play.data.validation.Check;
import play.data.validation.CheckWith;
import play.data.validation.MaxSize;
import play.data.validation.Required;
import play.db.jpa.*;

import javax.persistence.*;

import models.Project;
import models.User;
import models.validators.MustHaveUserIfNotSystem;


import java.util.*;

@Entity
public class ProjectTemplate extends BaseTemplate {	
	
	public ProjectTemplate(String name, Boolean isSystem, User user,
			Boolean needMembers, Boolean needClients) {
		super(name, isSystem, user);
		this.needMembers = needMembers;
		this.needClients = needClients;
	}

	@Required
	@MaxSize(20000)
	@Lob
	public String content;
	
	@OneToMany(mappedBy = "projectTemplate")
	public List<ProjectListTemplate> projectList;
	
	public Boolean needMembers;
    public Boolean needClients;
	
	@Override
	public String toString(){
		return (isSystem ? "[System] " : "") + name;
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
}
