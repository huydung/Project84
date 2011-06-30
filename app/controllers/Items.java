package controllers;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import com.huydung.utils.MiscUtil;

import models.Activity;
import models.Item;
import models.Listing;
import models.User;
import models.enums.ActivityAction;
import play.data.Upload;
import play.data.validation.Required;
import play.data.validation.Valid;
import play.data.validation.Validation;
import play.db.jpa.Blob;
import play.mvc.Before;
import play.mvc.Controller;
import play.mvc.Util;
import play.mvc.With;
import play.mvc.Http.Request;
import play.templates.JavaExtensions;

@With(Authorization.class)
public class Items extends AppController {
	
	@Before(priority=2,unless={"displayFile","restore"})
	protected static void setupListing(){

		Listing l = getActiveListing();
		if( l == null ){ error(400, "Bad Request"); }
		renderArgs.put("active", JavaExtensions.slugify(l.listingName));

	}	

	public static void updateCheck(
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
			item.update(true);
			renderText("Item " + item.name + " updated!");
		}catch(Exception e){error();}
	}
	
	public static void updateField(	@Required Long item_id){
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
					item.update(false);
					saved = true;				
				}else{error(400, "Bad Request");}
			}
			//Drag and Drop Date
			else if( field.equals("date") ){
				SimpleDateFormat sdf = new SimpleDateFormat(getLoggedin().dateFormat);				
				try {
					Date d = sdf.parse(parts[1]);
					item.date = d;
					item.update(false);
					saved = true;
				} catch (ParseException e) { error(400, "Bad Request");	}				
			}
			//Drag and Drop Category
			else if( field.equals("category") ){
				item.category = parts[1];
				item.update(false);
				saved = true;				
			}
		}else{error(400, "Bad Request");}
		
		if(saved){
			if( request.isAjax() ){
				render("items/item.html");
			}else{
				flash.put("success", "Item " + item.name + " updated!");	
				show(item_id);
			}
		}else{
			Listings.dashboard(item.listing.id);
		}
	}
	
	public static void show(@Required Long item_id){
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
			@Required Long listing_id, 			
			@Required String input){
		Item item = null;
		if( Validation.hasErrors() ){
			error(400, "Bad Request");
		}else{
			Listing l = getActiveListing();			
			item = Item.createFromSmartInput(input, l);
			item.creator = getLoggedin();
			if(!item.create()){
				displayValidationMessage();
			}				
			if( request.isAjax() ){
				render("items/item.html", item);
			}else{			
				flash.put("success", "Item " + item.name + " created!");
				Listings.dashboard(listing_id);
			}
		}		
		
	}
	
	public static void doCreateFromForm(
			@Required Long listing_id, 			
			@Valid Item item){
		if( Validation.hasErrors() ){
			displayValidationMessage();
			render("items/form.html", item);
		}else{		
			handleFile(item);
			try{
				item.create();
				flash.put("success", "Item " + item.name + " created!");			
			}catch(Exception e){error();}
		}
		if(params.get("redirectToCreate", Boolean.class)){	
			create(listing_id);
		}else{
			Listings.dashboard(listing_id);
		}
	}
	
	public static void edit(@Required Long item_id){
		if(Validation.hasErrors()){
			notFound();
		}
		Item item = getItem();		
		if( item == null ){	notFound("Item", item_id);	}		
		render("items/form.html", item);
	}
	
	public static void create(
			@Required Long listing_id){
		Item item = new Item(getActiveListing());		
		render("items/form.html", item);
	}
	
	public static void quickEdit(
			@Required Long item_id,
			@Required String input){
		if(Validation.hasErrors()){notFound();}
		Item item = getItem();
		if( item == null ){	notFound("Item", item_id);	}
		try{
			item.updateFromSmartInput(input);		
			item.update(false);
			if( request.isAjax() ){
				render("items/item.html", item);
			}else{
				flash.put("success", "Item " + item.name + " updated!");
				Listings.dashboard(item.listing.id);
			}
		}catch(Exception e){ error(); }
	}
	
	public static void doEdit(
			@Valid Item item,
			@Required Long item_id){
		if( Validation.hasErrors() ){
			displayValidationMessage();
			render("items/form.html", item);
		}
		handleFile(item);
		item.update(false);
		Listings.dashboard(item.listing.id);
	}
	
	@Util
	public static void handleFile(Item item){
		List<Upload> uploads = (List<Upload>) Request.current().args.get("__UPLOADS");
		if( uploads != null && uploads.size() > 0 ){
			Upload upload = uploads.get(0);
			if(upload.getSize() > 0){
				if( item.file != null && item.file.exists() ){
					MiscUtil.ConsoleLog("Delete old file");
					if( !item.file.getFile().delete() ){
						flash.put("warning", "The old file can not be deleted!");
					}
				}
				item.file_name = upload.getFileName();
				item.file_size = upload.getSize();
					
				item.file_ext = item.file_name.substring(item.file_name.lastIndexOf(".")+1);
				item.file = new Blob();
				item.file.set( upload.asStream() , upload.getContentType());
				MiscUtil.ConsoleLog("set new file");
			}			
		}
	}
	
	
	public static void delete(@Required Long item_id){
		if( Validation.hasErrors() ){ error(400, "Bad Request"); } 
		Item item = getItem();
		if( item == null ){	notFound("Item", item_id);	}
		item.delete();
		String message = "Item [" + item.name + "] has been deleted"; 
		if( request.isAjax() ){
			renderText(message);
		}else{
			flash.put("success", message);
			Listings.dashboard(item.listing.id);
		}
	}
	
	public static void restore(
			@Required Long item_id){
		if( Validation.hasErrors() ){ error(400, "Bad Request"); } 
		Item item = getItem();
		if( item == null ){	notFound("Item", item_id);	}
		item.restore();
		String message = "Item [" + item.name + "] has been restored to [" + item.listing.listingName + "]"; 
		if( request.isAjax() ){
			renderText(message);
		}else{
			flash.put("success", message);
			Projects.trash(item.project.id);
		}
	}
	
	public static void displayFile(Long item_id){
		Item item = Item.findById(item_id);
		if( item == null ){ notFound("Item", item_id); }
		if( item.file.exists() ){
			response.setContentTypeIfNotSet(item.file.type());
			renderBinary(item.file.get());
		}else{
			notFound();
		}
	}
}
