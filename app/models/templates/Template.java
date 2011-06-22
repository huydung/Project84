package models.templates;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;


import models.User;
import models.validators.MustHaveUserIfNotSystem;
import play.data.validation.CheckWith;
import play.data.validation.MaxSize;
import play.data.validation.Required;
import play.db.jpa.Model;

@Entity
public class Template extends Model{
	@Required
	public String name;
	@Required
	@MaxSize(255)
	public String description;
	
	@ManyToOne
	@CheckWith(MustHaveUserIfNotSystem.class)
	public User user;
	@Required
	public Boolean isSystem;
	
	public Template(String name, Boolean isSystem, User user) {
		super();
		this.name = name;
		this.user = user;
		this.isSystem = isSystem;
	}
	
	public Template(String name, Boolean isSystem, User user, String description) {
		this(name, isSystem, user);
		this.description = description;
	}
}
