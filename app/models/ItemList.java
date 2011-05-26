package models;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

import models.templates.ListTemplate;
import models.templates.ProjectListTemplate;

import play.data.validation.Required;
import play.db.jpa.Model;

@Entity
public class ItemList extends Model {
	@Required
	public String name;
	
	@Required
	public String description;
	
	@Required
	public String fields;
	
	@ManyToOne
	public Project project;

	public String basedOn;

	public ItemList(ListTemplate lt, String name, Project p) {
		super();
		this.basedOn = lt.name;
		this.name = name;
		this.project = p;
		this.fields = lt.fields;
		this.description = lt.description;
	}	
}
