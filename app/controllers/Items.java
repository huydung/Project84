package controllers;

import models.Item;
import models.Listing;
import models.enums.ActivityType;
import play.data.validation.Required;
import play.data.validation.Valid;
import play.data.validation.Validation;
import play.mvc.Before;
import play.mvc.Controller;
import play.templates.JavaExtensions;

public class Items extends AppController {
	
	@Before
	protected static void setupListing(){
		Long listing_id = params.get("listing_id", Long.class);
		if( listing_id == null ){ error(400, "Bad Request"); }
		Listing l = Listing.findById(listing_id);
		if( l == null ) { error(400, "Bad Request"); }
		renderArgs.put("l", l);
		renderArgs.put("active", JavaExtensions.slugify(l.listingName));
	}	
	
	protected static Listing getListing(){
		return renderArgs.get("l", Listing.class);
	}
	
	public static void updateCheck(
			@Required Long project_id, 
			@Required Long listing_id,
			@Required Long item_id,
			@Required Boolean checked){
		if(Validation.hasErrors()){
			error(400, "Bad Request");
		}
		Item item = Item.findById(item_id);
		if(item == null){
			notFound("Item", item_id);
		}
		item.checkbox = checked;
		item.save();
		item.log(ActivityType.CHANGE);
		renderText("Item " + item.name + " updated!");
	}
	
	public static void show(
			@Required Long project_id, 
			@Required Long listing_id,
			@Required Long item_id){
		if(Validation.hasErrors()){
			error(403, "Bad Request");
		}
		Item item = Item.findById(item_id);
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
			@Required Long id){
		if(Validation.hasErrors()){
			notFound();
		}
		Listing l = getListing();
		Item item;
		if( id != 0 ){
			item = Item.findById(id);
		}else{
			item = new Item(l);
		}
		if( item == null ){	notFound("Item", id);	}
		
		render("items/form.html", item, l);
	}
	
	public static void quickEdit(
			@Required Long project_id,
			@Required Long listing_id,
			@Required Long item_id,
			@Required String input){
		if(Validation.hasErrors()){notFound();}
		Item item = Item.findById(item_id);
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
			@Valid Item item){
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
			@Required Long id){
		if( Validation.hasErrors() ){
			notFound();
		}
		Item item = Item.findById(id);
		if( item == null ){
			notFound("Item", id);
		}
		item.delete();
		item.log(ActivityType.DELETE);
		Listings.dashboard(project_id, listing_id);
	}
}
