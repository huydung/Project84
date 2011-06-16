package controllers;

import models.Item;
import models.Listing;
import models.enums.ActivityType;
import play.data.validation.Required;
import play.data.validation.Valid;
import play.data.validation.Validation;
import play.mvc.Before;
import play.mvc.Controller;
import play.mvc.With;
import play.templates.JavaExtensions;

@With(Authorization.class)
public class Items extends AppController {
	
	@Before(priority=2)
	protected static void setupListing(){
		Listing l = getListing();
		if( l == null ){ error(400, "Bad Request"); }
		renderArgs.put("active", JavaExtensions.slugify(l.listingName));
	}	

	public static void updateCheck(
			@Required Long project_id, 
			@Required Long listing_id,
			@Required Long item_id,
			@Required Boolean checked){
		if(Validation.hasErrors()){
			error(400, "Bad Request");
		}
		Item item = getItem();
		if(item == null){
			notFound("Item", item_id);
		}
		item.checkbox = checked;
		item.save();
		item.log( checked ? ActivityType.CHECK : ActivityType.UNCHECK);
		renderText("Item " + item.name + " updated!");
	}
	
	public static void show(
			@Required Long project_id, 
			@Required Long listing_id,
			@Required Long item_id){
		if(Validation.hasErrors()){
			error(403, "Bad Request");
		}
		Item item = getItem();
		if(item == null){
			notFound("Item", item_id);
		}
		renderArgs.put("mode", "full");
		render(item);
	}
	
	public static void doCreate(
			@Required Long project_id,
			@Required Long listing_id, 			
			@Required String input){
		if( Validation.hasErrors() ){
			displayValidationMessage();
		}else{
			Listing l = getListing();
			
			Item item = Item.createFromSmartInput(input, l);
			item.creator = getLoggedin();
			if(!item.validateAndSave()){
				displayValidationMessage();
			}else{
				flash.put("success", "Item " + item.name + " created!");
			}
			item.log(ActivityType.CREATE);
		}
		
		if( request.isAjax() ){
			render("items/item.html");
		}else{
			String prevPath = params.get("prevPath"); 
			if(prevPath != null){
				redirect(prevPath);
			}else{
				Listings.dashboard(project_id, listing_id);
			}
		}
	}
	
	public static void edit(
			@Required Long project_id,
			@Required Long listing_id, 
			@Required Long item_id){
		if(Validation.hasErrors()){
			notFound();
		}
		Listing l = getListing();
		Item item = getItem();		
		if( item == null ){	notFound("Item", item_id);	}
		
		render("items/form.html", item, l);
	}
	
	public static void quickEdit(
			@Required Long project_id,
			@Required Long listing_id,
			@Required Long item_id,
			@Required String input){
		if(Validation.hasErrors()){notFound();}
		Item item = getItem();
		if( item == null ){	notFound("Item", item_id);	}
		Listing l = getListing();
		item.updateFromSmartInput(input);
		item.log(ActivityType.CHANGE);
		item.save();
		flash.put("success", "Item " + item.name + " updated!");
		Listings.dashboard(project_id, listing_id);
	}
	
	public static void doEdit(
			@Required Long project_id,
			@Required Long listing_id, 
			@Valid Item item,
			@Required Long item_id){
		if( Validation.hasErrors() ){
			displayValidationMessage();
			flash.keep();
			Validation.keep();
			edit(project_id, listing_id, item.id);
		}
		item.log(ActivityType.CHANGE);
		item.save();
		Listings.dashboard(project_id, listing_id);
	}
	
	public static void delete(
			@Required Long project_id,
			@Required Long listing_id, 
			@Required Long item_id){
		if( Validation.hasErrors() ){
			notFound();
		}
		Item item = getItem();
		if( item == null ){
			notFound("Item", item_id);
		}
		item.deleted = true;
		item.save();
		item.log(ActivityType.DELETE);
		Listings.dashboard(project_id, listing_id);
	}
}
