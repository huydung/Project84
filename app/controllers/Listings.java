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
			Boolean isDesc, String[] fields, 
			@Required Long project_id,
			@Required Long listing_id){
		
		//Correcting format of sorting string
		if(isDesc == null || isDesc == false){
			listing.sort += " ASC";
		}else{
			listing.sort += " DESC";
		}
		
		if( fields == null || fields.length < 1 ){
			displayValidationMessage();
			flash.put("info", "You must use at least 01 field");
			Projects.structure(project_id);
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
			flash.put("success", "Listing Configuration has been saved");
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
		
		/** FILTERING **/
		//Only set default parameters if in the params are currently have only 2 
		//keys which is id and project_id
		boolean setDefault = params.all().size() <=3 ;
		
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
		
		/** CATEGORIZING AND SORTING **/
		List<ItemField> orderable_fs = l.getOrderingFields();
		renderArgs.put("orderable_fs", orderable_fs);
		
		String sortString = "";
		String sort1 = params.get("sort_1");
		if( sort1 != null && !sort1.isEmpty() ){
			sortString = sort1;
		}else{
			if( orderable_fs.size() > 0 ){
				sort1 = orderable_fs.get(0).fieldName + " ASC";
				sortString += sort1;
				params.put("sort_1", sort1);
			}
		}
		String sort2 = params.get("sort_2");
		if( sort2 != null && !sort2.isEmpty() ){
			if( sortString.length() > 0 ){
				sortString += ", ";
			}
			sortString += sort2;
		}else{
			if( orderable_fs.size() > 1 ){
				sort2 = orderable_fs.get(1).fieldName + " ASC";
				sortString += ", " + sort2;
				params.put("sort_2", sort2);
			}
		}

		if(sortString.isEmpty()){ 
			sortString = l.sort;
		}else{
			sortString += ", " + l.sort;
		}
		
		/** FETCH THE ITEMS **/
		List<Item> items = Item.findByListing(l, filterString, sortString);
		
		/** PARSE THE ITEMS TO MULTIPLE LISTING FUNCTIONS **/
		
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
	
	public static void delete(@Required Long project_id, @Required Long listing_id){
		if(Validation.hasErrors()){
			error(400, "Bad Request");
		}
		Listing l = getListing();
		l.delete();
		flash.put("success", "Listing [" + l.listingName + "] and ALL its related items has been deleted");
		Projects.structure(project_id);
	}
}
