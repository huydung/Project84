package models.templates;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import com.huydung.utils.MiscUtil;

import models.Item;
import models.Listing;
import models.Project;
import models.User;
import models.validators.MustHaveUserIfNotSystem;



import play.data.validation.Check;
import play.data.validation.CheckWith;
import play.data.validation.MaxSize;
import play.data.validation.Required;
import play.db.jpa.JPA;
import play.i18n.Messages;

@Entity
public class ListTemplate extends Template {

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
	
	@OneToMany(mappedBy="lt")	
	public List<ItemListTemplate> items;
	
	@OneToMany(mappedBy = "listTemplate")
	public List<ProjectListTemplate> projectList;
	
	public static List<ListTemplate> getSystemTemplates(){
		return ListTemplate.find(
				"isSystem = ?", true).fetch();
	}
	
	@Override
	public String toString(){
		return this.name;
	}
	
	public static ListTemplate createFromListing(Listing l, User u){
		ListTemplate lt = new ListTemplate(l.listingName, false, u);
		lt.fields = l.fields;
		lt.hasTab = l.hasTab;
		lt.hasPermissions = l.hasPermissions;
		lt.iconPath = l.iconPath;
		lt.mainField = l.mainField;
		lt.subField = l.subField;
		lt.numItems = l.numItems;
		lt.sort = l.sort;	
		return lt;
	}
	
	public Item addItem(Item i){
		ItemListTemplate itl = new ItemListTemplate(i, this);
		itl.save();
		return i;
	}
	
	public boolean addItems(List<Item> items){
		if( items != null && items.size() > 0 ){
			String insertQuery = "INSERT INTO ItemListTemplate(item_id, lt_id) VALUES ";
			for(Item item : items){
				insertQuery += "("+ item.id + "," + this.id +"),";												
			}
			insertQuery = insertQuery.substring(0, insertQuery.length() - 1);
			MiscUtil.ConsoleLog(insertQuery);
			JPA.em().createNativeQuery(insertQuery).executeUpdate();
		}
		return true;
	}
	
}
