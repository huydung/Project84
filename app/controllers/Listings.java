package controllers;

import java.util.ArrayList;

import com.huydung.utils.ItemField;

import models.Listing;
import play.data.validation.Required;
import play.data.validation.Valid;
import play.mvc.Controller;

public class Listings extends AppController {
	public static void doCreate(Long template_id, String name, Long project_id){
		
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
}
