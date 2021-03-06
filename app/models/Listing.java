package models;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import models.enums.ActivityAction;
import models.features.BasicFeature;
import models.features.CalculateFeature;
import models.features.CalculateMethod;
import models.filters.BasicFilter;
import models.filters.FilterFactory;
import models.templates.ItemListTemplate;
import models.templates.ListTemplate;

import org.hibernate.annotations.Filter;

import play.Play;
import play.cache.Cache;
import play.data.validation.Check;
import play.data.validation.CheckWith;
import play.data.validation.Min;
import play.data.validation.Required;
import play.db.jpa.Model;
import play.i18n.Messages;
import play.mvc.Router;
import play.templates.JavaExtensions;

import com.huydung.utils.ItemField;
import com.huydung.utils.Link;
import com.huydung.utils.MiscUtil;

import controllers.AppController;

@Entity
@Filter(name="deleted")
public class Listing extends Model implements IWidget, IActivityLoggabe {
	private static final long serialVersionUID = 1L;
	
	@Required
	public Integer ordering = -10;

	@Required
	public String iconPath = "/public/appicons/text.png";
	
	@Required
	public String listingName;
	
	@Required
	public String fields = "";
	
	@ManyToOne
	@Required
	public Project project;
	
	public Boolean deleted = false;
	
	@Required
	public Boolean hasTab = true;
	
	@Required
	public Boolean hasPermissions = true;
	
	@CheckWith(MustInFields.class)
	public String mainField = "name";
	
	@CheckWith(MustInFields.class)
	public String subField;
	
	@Min(1)
	public Integer numItems = 5;
	
	@Required
	public String sort = "created DESC";
	
	@OneToMany(mappedBy="listing")
	@Filter(name="deleted")
	public List<Item> items;
		
	public Listing(String listingName) {
		super();
		this.listingName = listingName;
	}
	
	public Listing(String name, Project p) {
		super();
		this.listingName = name;
		this.project = p;
	}	

	static class MustInFields extends Check {
		public boolean isSatisfied(Object o, Object field) {
    	  Listing lt = (Listing)o;
    	  
    	  if(lt.fields != null){
    		  return lt.hasField((String)field);
    	  }else{
    		  return false;
    	  }
		}	
	}	
	
	public boolean validateAndSave(User user){
		boolean res = super.validateAndSave();
		if( res ){
			Activity.track(this, user, ActivityAction.CHANGE);
		}
		return res;
	}
	
	public boolean validateAndCreate(User user){
		boolean res = super.validateAndCreate();
		if( res ){
			Activity.track(this, user, ActivityAction.CREATE);
		}
		return res;
	}
	
	public Listing delete(User user){
		return delete(user, true);
	}
	
	public Listing delete(User user, boolean log){
		this.deleted = true;
		this.save();
		for( Item item : this.items ){
			item.delete();
		}
		if(log){ 
			Activity.track(this, user, ActivityAction.DELETE);
		}
		return this;
	}
	
	public void setItemFields(List<ItemField> fields){
		this.fields = "";
		for(ItemField f : fields){
			this.fields += f.fieldName + ":" + f.name + ",";			
		}
		if( this.fields.length() > 0 ){
			this.fields = this.fields.substring(0, this.fields.length() - 1 );
		}
	}
	
	public List<ItemField> getItemFields(){
		ArrayList<ItemField> fs = new ArrayList<ItemField>(); 		
		if( this.fields.length() > 0 ){
			String[] fieldStr = this.fields.split(",");
			for(String f : fieldStr){
				String[] parts = f.split(":");
				//if( !parts[0].equals("name") ){
					fs.add(new ItemField(parts[0], parts[1]));
				//}
			}
		}
		return fs;
	}
	
	public List<ItemField> getOrderingFields(){
		ArrayList<ItemField> fs = new ArrayList<ItemField>(); 		
		if( this.fields.length() > 0 ){
			String[] fieldStr = this.fields.split(",");
			for(String f : fieldStr){
				String[] parts = f.split(":");
				if( Item.FIELDS_ORDERABLE.contains("," + parts[0] +",") ){
					fs.add(new ItemField(parts[0], parts[1]));
				}
			}
		}
		return fs;
	}
	
	public boolean hasField(String fName){
		int index = fName.indexOf("_");
		  if( index > 0 ){
			fName = fName.substring(0, index);
		  }
		boolean res = this.fields.contains(fName + ":") || Item.FIELDS_REQUIRED.contains("," + fName + ",");
		return res;
	}
	
	public String getFieldName(ItemField f){
		return getFieldName(f.fieldName);
	}
	
	public String getFieldName(String fieldName){
		String fn = fieldName;
		if(fn.contains("_")){
			fn = fn.substring(0, fn.indexOf("_"));
		}
		if( this.fields.length() > 0 ){
			String[] fieldStr = this.fields.split(",");
			for(String fs : fieldStr){
				String[] parts = fs.split(":");
				if( parts[0].equals(fn) ){
					return parts[1];
				}
			}
		}
		return Messages.get("f."+fn);
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
	
	public static List<String> getIcons(){
		List<String> paths = (List<String>)Cache.get("iconPaths");
		if( paths == null ){
			paths = new ArrayList<String>();
			String sep = System.getProperty("file.separator");
			String baseDir = Play.applicationPath + sep + "public" + sep + "appicons";
			String baseUrl = "/public/appicons/";
			File folder = new File( baseDir );
			
			if( folder != null && folder.isDirectory() ){
				File [] icons = folder.listFiles(new FilenameFilter() {					
					@Override
					public boolean accept(File arg0, String arg1) {						
						return arg1.endsWith(".png");
					}
				});
				for( File icon : icons ){
					paths.add(baseUrl + icon.getName());
				}
			}
			Cache.set("iconPaths", paths);
			MiscUtil.ConsoleLog("Put iconPaths into Cache");
		}else{
			MiscUtil.ConsoleLog("Get iconPaths from Cache");
		}
		return paths;
	}
	
	public List<BasicFilter> getFilters(){
		List<BasicFilter> filters = new ArrayList<BasicFilter>();
		List<ItemField> fields = this.getItemFields();
		for(ItemField f : fields){
			BasicFilter filter = FilterFactory.createFilter(f, this);
			if( filter != null ){
				filters.add( filter );
			}
		}
		return filters;
	}
	
	public List<BasicFeature> getFeatures(){
		List<BasicFeature> features = new ArrayList<BasicFeature>();
		if( this.hasField("cost") ){
			features.add( new CalculateFeature(CalculateMethod.SUM, "cost_total", this) );
			features.add( new CalculateFeature(CalculateMethod.AVERAGE, "cost_total", this) );
		}
		if( this.hasField("number") ){
			features.add( new CalculateFeature(CalculateMethod.SUM, "number", this) );
			features.add( new CalculateFeature(CalculateMethod.AVERAGE, "number", this) );		
		}
		return features;
	}
	
	public List<String> getCategories(){
		return Item.find("SELECT DISTINCT category FROM Item i WHERE listing = ? AND category IS NOT NULL", this).fetch();
	}
	
	public static Listing findByProjectAndName(Project project, String name){
		return Listing.find("project = ? AND listingName = ?", project, name).first();
	}

	@Override
	public String getWidgetName() {
		return this.listingName;
	}

	@Override
	public String getSubName() {
		return this.getFieldName(this.subField);
	}

	@Override
	public String getHtmlClass() {		
		return JavaExtensions.slugify(this.listingName);
	}

	@Override
	public Link getFirstLink() {
		Map<String, Object> args = new HashMap<String, Object>();
		args.put("listing_id", this.id);
		return new Link( 
				Messages.get("labels.viewAll"),
				Router.getFullUrl("Listings.dashboard", args),
				"icon-link-list"
		);
	}

	@Override
	public Link getLastLink() {
		Map<String, Object> args = new HashMap<String, Object>();
		args.put("listing_id", this.id);
		return new Link( 
				Messages.get("labels.create"),
				Router.getFullUrl("Items.create", args),
				"icon-link-add"
		);
	}

	@Override
	public List getItems() {		
		return Item.findByListing(this, "", sort, numItems);
	}

	@Override
	public String getHtml() {
		return null;
	}

	@Override
	public int getColSpan() {		
		return 1;
	}
	
	public String getDescription(){
		String des = Messages.get("labels.listing.fieldsShort") + " ";
		List<ItemField> fields = getItemFields();
		for( ItemField f : fields ){
			des += " " + f.name + " (" + f.fieldName + "),";
		};
		des = des.substring(0, des.length());
		return des;
	}
	
	public boolean addItems(User user, List<ItemListTemplate> itls){
		if(itls!=null){
			for( ItemListTemplate itl : itls ){
				Item item = new Item(this);
				item.copyProperties(itl.item);
				//Make sure the constrained fields are respected
				item.creator = user;
				item.project = this.project;
				item.listing = this;
				//Make sure user is null, because this is a new project
				item.user = null;
				//Reset timestamp
				item.created = new Date();
				item.updated = new Date();
				//Make sure the item is in active state even if the original item is marked to be deleted
				item.deleted = false;
				item.save();
				MiscUtil.ConsoleLog("Saved item " + item.id + ".");
			}		
		}
		return true;
	}

	@Override
	public String getType() {
		return "Listing";
	}

	@Override
	public Project getProject() {
		return this.project;
	}
	
	@Override
	public String getActivityShowLink() {
		Map<String, Object> args = new HashMap<String, Object>();
		args.put("listing_id", id);
		return Router.getFullUrl("Listings.dashboard", args);
	}
	
	@Override
	public String getLogName() {
		return this.listingName;
	}
}
