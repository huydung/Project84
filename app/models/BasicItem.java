package models;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;


import play.data.validation.Required;
import play.db.jpa.Model;

@Entity
public class BasicItem extends Model {
	@Required
	public Date created;
	@ManyToOne
	@Required
	public User creator;
	@Required
	public Date updated;
	@Required
	public String type;
	@Required
	public String name;

	public BasicItem() {
		super();
		this.updated = new Date();
	}	
	
}
