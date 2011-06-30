package controllers;

import java.util.HashMap;
import java.util.Map;

import models.Comment;
import models.Item;
import models.Listing;
import models.Project;
import play.data.validation.Required;
import play.data.validation.Valid;
import play.data.validation.Validation;
import play.mvc.Controller;
import play.mvc.Router;
import play.mvc.With;

@With(Authorization.class)
public class Comments extends AppController {

	public static void doCreate(@Required Long project_id,@Required Long listing_id, @Valid Comment comment){
		if(Validation.hasErrors()){ error(400, "Bad Request"); }
		if(comment.parent == null){
			notFound();
		}		
		comment.creator = getLoggedin();
		if( comment.create() ){
			flash.put("success", "Your comment has been successfully created");
		}else{
			displayValidationMessage();
			Validation.keep();
			params.flash();
		}		
		Items.show(comment.parent.id);
	}
	
	public static void delete(@Required Long project_id, @Required Long listing_id, @Required Long comment_id){
		if(Validation.hasErrors()){ error(400, "Bad Request"); }
		Comment comment = Comment.findById(comment_id);
		if( comment == null ) { notFound("Comment", comment_id); }
		comment.delete();
		flash.put("success", "The comment [" + comment.name + "] has been deleted");
		Items.show(comment.parent.id);
	}
}
