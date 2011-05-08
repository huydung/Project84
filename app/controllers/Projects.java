package controllers;

import models.Project;
import models.User;
import play.data.validation.Valid;
import play.mvc.*;

public class Projects extends AppController {    
    
    public static void index() {
        render();
    }
    
    public static void create(Long id){
    	if(id != null){
    		//Load project in the form
    	}
    	render();
    }

}
