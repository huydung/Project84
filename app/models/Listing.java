package models;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

import com.huydung.utils.ItemField;
import com.huydung.utils.MiscUtil;

import models.templates.ListTemplate;
import models.templates.ProjectListTemplate;

import play.Play;
import play.cache.Cache;
import play.data.validation.Check;
import play.data.validation.CheckWith;
import play.data.validation.Required;
import play.db.jpa.Model;
import play.i18n.Messages;

@Entity
public class Listing extends Model {

	@Required
	public String iconPath = "/public/appicons/text.png";
	
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
	public Boolean hasPermissions = true;
	
	@CheckWith(MustInFields.class)
	public String mainField;
	
	@CheckWith(MustInFields.class)
	public String subField;

	public Integer numItems = 5;
	
	@Required
	public String sort = "created DESC";
		
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
				fs.add(new ItemField(parts[0], parts[1]));
			}
		}
		return fs;
	}
	
	public boolean hasField(String fName){
		int index = fName.indexOf("_");
		  if( index > 0 ){
			fName = fName.substring(0, index);
		  }
		return this.fields.contains(fName + ":") || Item.getRequiredFields().contains(fName);
	}
	
	public String getFieldName(ItemField f){
		if( this.fields.length() > 0 ){
			String[] fieldStr = this.fields.split(",");
			for(String fs : fieldStr){
				String[] parts = fs.split(":");
				if( parts[0].equals(f.fieldName) ){
					return parts[1];
				}
			}
		}
		return "";
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
}
