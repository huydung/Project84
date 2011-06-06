package models;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

import com.huydung.utils.ItemField;

import models.templates.ListTemplate;
import models.templates.ProjectListTemplate;

import play.data.validation.Check;
import play.data.validation.CheckWith;
import play.data.validation.Required;
import play.db.jpa.Model;

@Entity
public class Listing extends Model {

	@Required
	public String iconPath;
	
	@Required
	public String listingName;
	
	@Required
	public String fields = "";
	
	@ManyToOne
	@Required
	public Project project;
	
	@Required
	public Boolean hasTab = true;
	
	@Required
	public Boolean hasPermissions = false;
	
	@CheckWith(MustInFields.class)
	public String mainField;
	
	@CheckWith(MustInFields.class)
	public String subField;

	public Integer numItems = 5;
	
	public String sort;
		
	public Listing(String listingName) {
		super();
		this.listingName = listingName;
	}

	static class MustInFields extends Check {
		public boolean isSatisfied(Object o, Object field) {
    	  Listing lt = (Listing)o;
    	  return lt.fields.contains((String)field+":");
		}	
	}
	
	public Listing(String name, Project p) {
		super();
		this.listingName = name;
		this.project = p;
	}	
	
	public void setItemFields(List<ItemField> fields){
		this.fields = "";
		for(ItemField f : fields){
			this.fields += f.getFieldName() + ":" + f.getName() + ",";			
		}
		if( this.fields.length() > 0 ){
			this.fields = this.fields.substring(this.fields.length() - 1 );
		}
	}
	
	public List<ItemField> getItemFields(){
		ArrayList<ItemField> fs = new ArrayList<ItemField>(); 
		if( this.fields.length() > 0 ){
			String[] fieldStr = this.fields.split(",");
			for(String f : fieldStr){
				String[] parts = f.split(":");
				fs.add(new ItemField(parts[0], parts[1]));
			}
		}
		return fs;
	}
	
	public static Listing createFromTemplate(ListTemplate lt){
		Listing l = new Listing(lt.name);
		l.fields = lt.fields;
		l.hasTab = lt.hasTab;
		l.hasPermissions = lt.hasPermissions;
		l.iconPath = lt.iconPath;
		l.mainField = lt.mainField;
		l.subField = lt.subField;
		l.numItems = lt.numItems;
		l.sort = lt.sort;		
		return l;
	}
	
	public static Listing createFromTemplate(ListTemplate lt, Project p){
		Listing l = createFromTemplate(lt);
		l.project = p;
		return l;
	}
}
