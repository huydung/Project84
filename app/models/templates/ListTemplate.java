package models.templates;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import models.Project;
import models.User;
import models.validators.MustHaveUserIfNotSystem;



import play.data.validation.Check;
import play.data.validation.CheckWith;
import play.data.validation.MaxSize;
import play.data.validation.Required;
import play.i18n.Messages;

@Entity
public class ListTemplate extends BaseTemplate {

	public ListTemplate(String name, Boolean isSystem, User user) {
		super(name, isSystem, user);
	}
	
	@Required
	public String iconPath;
	
	@Required
	public String fields = "";
	
	@Required
	public Boolean hasTab = true;
	
	@Required
	public Boolean hasPermissions = false;
	
	public String mainField;

	public String subField;

	public Integer numItems = 5;
	
	public String sort = "created DESC";
	
	@OneToMany(mappedBy = "listTemplate")
	public List<ProjectListTemplate> projectList;
	
	public static List<ListTemplate> getTemplates(User user){
		return ListTemplate.find(
				"isSystem = ? OR (isSystem = ? AND user = ?)", true, false, user).fetch();
	}
	
	@Override
	public String toString(){
		return (isSystem ? "[" +  Messages.get("labels.system") + "] " : "") + this.name;
	}
}
