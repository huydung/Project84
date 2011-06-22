package controllers;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import models.Item;
import models.Listing;
import models.User;
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
		try{
			item.save();
			item.log( checked ? ActivityType.CHECK : ActivityType.UNCHECK);
			renderText("Item " + item.name + " updated!");
		}catch(Exception e){error();}
	}
	
	public static void updateField(
			@Required Long project_id, 
			@Required Long listing_id,
			@Required Long item_id){
		if(Validation.hasErrors()){
			error(400, "Bad Request");
		}
		Item item = getItem();
		if(item == null){
			notFound("Item", item_id);
		};
		boolean saved = false;
		String data = params.get("data");
		if( data != null ){
			String[] parts = data.split(":");
			if( parts.length != 2 ){
				error(400, "Bad Request");
			}
			String field = parts[0];
			//Drag and Drop User
			if( field.equals("user.id") ){
				Long id = Long.parseLong(parts[1]); if(id == null){error(400, "Bad Request");}
				item.user = User.findById(id);
				if( item.user != null ){
					item.update();
					saved = true;				
				}else{error(400, "Bad Request");}
			}
			//Drag and Drop Date
			else if( field.equals("date") ){
				SimpleDateFormat sdf = new SimpleDateFormat(getLoggedin().dateFormat);				
				try {
					Date d = sdf.parse(parts[1]);
					item.date = d;
					item.update();
					saved = true;
				} catch (ParseException e) { error(400, "Bad Request");	}				
			}
			//Drag and Drop Category
			else if( field.equals("category") ){
				item.category = parts[1];
				item.update();
				saved = true;				
			}
		}else{error(400, "Bad Request");}
		
		if(saved){
			if( request.isAjax() ){
				render("items/item.html");
			}else{
				flash.put("success", "Item " + item.name + " updated!");	
				show(project_id, listing_id, item_id);
			}
		}else{
			Listings.dashboard(project_id, listing_id);
		}
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
		Item item = null;
		if( Validation.hasErrors() ){
			displayValidationMessage();
		}else{
			Listing l = getListing();			
			item = Item.createFromSmartInput(input, l);
			item.creator = getLoggedin();
			if(!item.create()){
				displayValidationMessage();
			}				
		}
		
		if( request.isAjax() ){
			render("items/item.html", item);
		}else{			
			flash.put("success", "Item " + item.name + " created!");
			Listings.dashboard(project_id, listing_id);
		}
	}
	
	public static void doCreateFromForm(
			@Required Long project_id,
			@Required Long listing_id, 			
			@Valid Item item){
		if( Validation.hasErrors() ){
			displayValidationMessage();
			render("items/form.html", item);
		}else{		
			try{
				item.create();
				flash.put("success", "Item " + item.name + " created!");			
			}catch(Exception e){error();}
		}
		if(params.get("redirectToCreate", Boolean.class)){	
			create(project_id, listing_id);
		}else{
			Listings.dashboard(project_id, listing_id);
		}
	}
	
	public static void edit(
			@Required Long project_id,
			@Required Long listing_id, 
			@Required Long item_id){
		if(Validation.hasErrors()){
			notFound();
		}
		Item item = getItem();		
		if( item == null ){	notFound("Item", item_id);	}		
		render("items/form.html", item);
	}
	
	public static void create(
			@Required Long project_id,
			@Required Long listing_id){
		Item item = new Item(getListing());		
		render("items/form.html", item);
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
		try{
			item.updateFromSmartInput(input);		
			item.update();
			if( request.isAjax() ){
				render("items/item.html", item);
			}else{
				flash.put("success", "Item " + item.name + " updated!");
				Listings.dashboard(project_id, listing_id);
			}
		}catch(Exception e){ error(); }
	}
	
	public static void doEdit(
			@Required Long project_id,
			@Required Long listing_id, 
			@Valid Item item,
			@Required Long item_id){
		if( Validation.hasErrors() ){
			displayValidationMessage();
			render("items/form.html", item);
		}
		item.update();
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
		item.delete();
		if( request.isAjax() ){
			renderText("Deleted");
		}else{
			Listings.dashboard(project_id, listing_id);
		}
	}
}
