package controllers;

import models.Item;
import models.Listing;
import play.data.validation.Required;
import play.data.validation.Validation;
import play.mvc.Controller;

public class Items extends AppController {
	public static void show(@Required Long project_id, @Required Long listing_id){
		renderText("");
	}
	
	public static void doCreate(
			@Required Long listing_id, 
			@Required Long project_id,
			@Required String input){
		if( Validation.hasErrors() ){
			displayValidationMessage();
			render("listings/item.html");
		}
		Listing l = Listing.findById(listing_id);
		if( l == null ){ notFound("Listing", listing_id); }
		
		Item item = Item.createFromSmartInput(input, l);
		if(!item.validateAndSave()){
			displayValidationMessage();
		}		
		render("listings/item.html");
	}
}
