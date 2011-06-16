package controllers;

import java.util.ArrayList;
import java.util.List;

import com.huydung.utils.ItemField;
import com.huydung.utils.MiscUtil;

import models.Item;
import models.Listing;
import models.Project;
import models.filters.BasicFilter;
import models.templates.ListTemplate;
import play.data.validation.Required;
import play.data.validation.Valid;
import play.data.validation.Validation;
import play.mvc.Controller;
import play.mvc.With;
import play.templates.JavaExtensions;

@With(Authorization.class)
public class Listings extends AppController {
	
	public static void saveOrderings(@Required Long project_id, String listing_ids){
		String[] ids = listing_ids.split(",");
		int i = 1;
		Project p = getActiveProject();
		for(String id : ids){
			Listing l = Listing.findById(Long.parseLong(id));
			if( l!= null && l.project == p ){
				l.ordering = i;
				l.save();
				i++;
			}			
		}
		renderText("OK");
	}
	
	public static void doCreate(@Required Long template_id, @Required String name, @Required Long project_id){
		if( Validation.hasErrors() ){
			displayValidationMessage();			
		}else{
			ListTemplate lt = ListTemplate.findById(template_id);
			if( lt == null ){
				Validation.addError("template_id", "Template not found");
				displayValidationMessage();
			}			
			getActiveProject().addListing(lt, name);
		}
		
		Projects.structure(project_id);
	}
	
	public static void doEdit(Listing listing,
			Boolean isDesc, String[] fields, @Required Long project_id){
		//Correcting format of sorting string
		if(isDesc == null || isDesc == false){
			listing.sort += " ASC";
		}else{
			listing.sort += " DESC";
		}
		
		//Get fields configuration
		ArrayList<ItemField> itfs = new ArrayList<ItemField>();
		for(String f: fields){
			String fname = params.get("field_"+f+"_name");
			ItemField itf = new ItemField(f);
			if(fname != null && !fname.isEmpty()){
				itf.name = fname;
			}			
			itfs.add(itf);
		}
		listing.setItemFields(itfs);
		
		if (!listing.validateAndSave()){
			displayValidationMessage();
			Projects.structure(project_id);
		}else{
			Projects.structure(project_id);
		}
	}
	
	public static void dashboard(
			@Required Long project_id, 
			@Required Long listing_id){
		
		if( Validation.hasErrors() ){
			notFound();
		}
		
		Listing l = getListing();
		
		//Only set default parameters if in the params are currently have only 2 
		//keys which is id and project_id
		int size = params.all().size();
		boolean setDefault = size <=3 ;
		
		String filterString = "";
		List<BasicFilter> filters = l.getFilters();
		for( BasicFilter bf : filters ){
			if(setDefault) { 
				bf.setDefault(params); 
				MiscUtil.ConsoleLog("setting default for filter" + bf.toString());
			}
			String query = bf.getJPQL(params);
			if( query != null && !query.isEmpty() ){
				filterString += query + " AND";
			}			
		}
		if( filterString.length() > 0 ){
			filterString = filterString.substring(0, filterString.length() - 4);
		}		
		
		List<Item> items = Item.findByListing(l, filterString);
		
		renderArgs.put("active", JavaExtensions.slugify(l.listingName, true));
		render(filters, items, l);
	}
	
	public static void getCategories(@Required Long listing_id){
		if(Validation.hasErrors()){
			notFound();
		}
		Listing l = Listing.findById(listing_id);
		if( l == null ){
			notFound("Listing", listing_id);
		}
		List<String> categories = l.getCategories();
		renderJSON(categories);
	}
}
