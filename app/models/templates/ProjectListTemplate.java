package models.templates;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import play.data.validation.Required;
import play.db.jpa.Model;

@Entity
public class ProjectListTemplate extends Model {
	@Required
	@ManyToOne
	public ProjectTemplate projectTemplate;
	
	@Required
	@ManyToOne
	public ListTemplate listTemplate;
	
	@Required
	public String name; 
	
	public ProjectListTemplate(ProjectTemplate projectTemplate,
			ListTemplate listTemplate, String name) {
		super();
		this.projectTemplate = projectTemplate;
		this.listTemplate = listTemplate;
		this.name = name;
	}

	public static ProjectListTemplate findByProjectAndListTemplate(ProjectTemplate pt, ListTemplate lt){
		return ProjectListTemplate.find(
				"projectTemplate = ? AND listTemplate = ?", pt, lt).first();
	}
	
	public static List<ProjectListTemplate> findByProjectTemplate(ProjectTemplate pt){
		return ProjectListTemplate.find(
				"projectTemplate = ?", pt).fetch();
	}
	
	public static List<ProjectListTemplate> findByListTemplate(ListTemplate lt){
		return ProjectListTemplate.find(
				"listTemplate = ?", lt).fetch();
	}
}
