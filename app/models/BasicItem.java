package models;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.ManyToOne;
import javax.persistence.PostPersist;

import org.hibernate.annotations.Filter;


import play.data.validation.Required;
import play.db.jpa.Model;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Filter(name="deleted")
public class BasicItem extends Model {
	
	public Date created;
	@ManyToOne
	@Required
	public User creator;
	public Date updated;
	@Required
	public String name;
	
	public Boolean deleted = false; 

	public BasicItem() {
		super();
	}	
	
	@PostPersist
	public void beforeSave(){
		if(this.created == null){
			this.created = new Date();			
		}
		this.updated = new Date();
	}
	
}
