package models.templates;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import models.User;
import models.validators.MustHaveUserIfNotSystem;



import play.data.validation.Check;
import play.data.validation.CheckWith;
import play.data.validation.MaxSize;
import play.data.validation.Required;

@Entity
public class ListTemplate extends BaseTemplate {

	public ListTemplate(String name, Boolean isSystem, User user, String fields, Boolean hasTab) {
		super(name, isSystem, user);
		this.fields = fields;
		this.hasTab = hasTab;
	}
	@Required
	public String fields;
	
	@Required
	public Boolean hasTab;	
	
	@OneToMany(mappedBy = "listTemplate")
	public List<ProjectListTemplate> projectList;
}
