package controllers;

import play.data.validation.Required;
import play.mvc.Controller;

public class Items extends Controller {
	public static void show(@Required Long project_id, @Required Long id){
		renderText("");
	}
}
