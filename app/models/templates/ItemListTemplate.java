package models.templates;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import models.Item;
import play.data.validation.Required;
import play.db.jpa.Model;
@Entity
public class ItemListTemplate extends Model {
	@Required 
	@ManyToOne
	public Item item;
	
	@Required
	@ManyToOne
	public ListTemplate lt;

	public ItemListTemplate(Item item, ListTemplate lt) {
		super();
		this.item = item;
		this.lt = lt;
	}
	
	
}
