package models.templates;

import play.*;
import play.data.validation.Check;
import play.data.validation.CheckWith;
import play.data.validation.MaxSize;
import play.data.validation.Required;
import play.db.jpa.*;
import play.i18n.Messages;
import play.mvc.Scope.Params;

import javax.persistence.*;

import com.huydung.utils.MiscUtil;

import models.Item;
import models.Listing;
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
	
	public static List<ProjectTemplate> getTemplates(User user, Boolean userOnly){
		if( userOnly ){
			return ProjectTemplate.find(
					"(isSystem = ? AND user = ?)", false, user).fetch();
		}else{
			return ProjectTemplate.find(
					"isSystem = ? OR (isSystem = ? AND user = ?)", true, false, user).fetch();
		}
	}
	
	public void addListTemplate(ListTemplate lt, String name){
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
	
	public static ProjectTemplate createFromProject(Project p, String name, Long[] listings, User u, Params params){
		ProjectTemplate pt = new ProjectTemplate(name, false, u, p.needMembers);
		pt.save();
		
		for( Long id : listings ){
			Listing l = Listing.findById(id);
			if( l != null ){
				ListTemplate lt = ListTemplate.createFromListing(l, u);
				lt.save();
				pt.addListTemplate(lt, l.listingName);
				String key = "listing-" + l.id + ".includeItems";
				Boolean includeItems = params.get(key, Boolean.class);
				
				if( includeItems ){
					lt.addItems(l.items);
				}
				
			}			
		}
		return pt;
	}
}
