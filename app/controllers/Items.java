package controllers;

import models.Item;
import models.Listing;
import play.data.validation.Required;
import play.data.validation.Valid;
import play.data.validation.Validation;
import play.mvc.Controller;

public class Items extends AppController {
	public static void show(@Required Long project_id, @Required Long listing_id){
		renderText("");
	}
	
	public static void doCreate(
			@Required Long project_id,
			@Required Long listing_id, 			
			@Required String input){
		if( Validation.hasErrors() ){
			displayValidationMessage();
		}else{
			Listing l = Listing.findById(listing_id);
			if( l == null ){ notFound("Listing", listing_id); }
			
			Item item = Item.createFromSmartInput(input, l);
			item.creator = getLoggedin();
			if(!item.validateAndSave()){
				displayValidationMessage();
			}
		}
		if( request.isAjax() ){
			render("items/item.html");
		}else{
			params.flash();
			Listings.dashboard(project_id, listing_id);
		}
	}
	
	public static void edit(
			@Required Long project_id,
			@Required Long listing_id, 
			@Required Long id){
		renderText("Edit Form");
	}
	
	public static void quickEdit(
			@Required Long project_id,
			@Required Long listing_id,
			@Required Long item_id,
			@Required String input){
		if(Validation.hasErrors()){
			notFound();
		}
		Item item = Item.findById(item_id);
		if( item == null ){	notFound("Item", item_id);	}
		Listing l = Listing.findById(listing_id);
		if(l == null){ notFound("Listing", listing_id); }
		item.updateFromSmartInput(input);
		item.save();
		flash.put("success", "Item " + item.name + " updated!");
		Listings.dashboard(project_id, listing_id);
	}
	
	public static void doEdit(
			@Required Long project_id,
			@Required Long listing_id, 
			@Valid Item item){
		renderText("Edit Form");
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
		Listings.dashboard(project_id, listing_id);
	}
}
