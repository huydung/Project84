package controllers;

import java.util.List;

import play.data.validation.Required;
import play.data.validation.Validation;

import models.BasicItem;
import models.Project;

public class BasicItems extends AppController {
	
	public static void search(@Required Long project_id, @Required String keyword){
		if( Validation.hasErrors()  ){
			error(400, "Bad Request");	
		}
		Project p = getActiveProject();
		if( p == null ){ error(400, "Bad Request");	 }
		List<BasicItem> results = BasicItem.search(p, keyword);
		//params.flash();
		render( "basicitems/search_results.html", results );
	}
	
	public static void advancedSearch(){
		
	}
}
