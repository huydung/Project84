package models;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.ManyToOne;
import javax.persistence.PostPersist;
import javax.persistence.PostRemove;
import javax.persistence.PrePersist;
import javax.persistence.PreRemove;

import org.bouncycastle.asn1.x509.qualified.TypeOfBiometricData;
import org.hibernate.annotations.Filter;


import play.data.validation.Required;
import play.db.jpa.JPA;
import play.db.jpa.Model;
import play.templates.JavaExtensions;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Filter(name="deleted")
public class BasicItem extends Model {
	
	public Date created = null;
	@ManyToOne
	public User creator = null;
	public Date updated;
	@Required
	public String name;
	
	public Boolean deleted = false; 

	public BasicItem() {
		super();
	}	
	
	@PrePersist
	public void beforeSave(){
		if(this.created == null){
			this.created = new Date();			
		}
		this.updated = new Date();
	}
	
	@PreRemove
	public void deleteComments(){		
		JPA.em().createQuery("DELETE Comment WHERE parent_id = " + this.id).executeUpdate();
	}
	
	public static List<BasicItem> search(Project project, String keyword){
		keyword = "%" + keyword + "%"; 
		List<Comment> comments = Comment.find("project = ? AND (name LIKE ? OR body LIKE ?)", project, keyword, keyword).fetch(100);
		List<Item> items = Item.find("project = ? AND (rawInput LIKE ? OR description LIKE ? OR body LIKE ?)", project,keyword, keyword, keyword).fetch(100);
		List<BasicItem> results = new ArrayList<BasicItem>();
		results.addAll(comments);
		results.addAll(items);
		return results;
	}
	
	public String getType(){
		Class clazz = this.getClass();
		if( clazz != Item.class ){
			return clazz.getSimpleName();
		}else{
			return JavaExtensions.slugify(((Item)this).listing.listingName);
		}
	}
}
