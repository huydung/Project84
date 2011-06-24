package controllers;


import jobs.Bootstrap;
import play.mvc.Before;
import models.User;

public class Admin extends AppController {
    
	@Before(priority = 20)
	public static void checkAdminRight(){
		User user = getLoggedin();
    	if( user == null ){
    		error(403, "Access Denied");
    	}
    	if( !user.email.equals("contact.huydung@gmail.com") ){
    		error(403, "Access Denied");
    	}
	}
	
    public static void admin(){    	
    	render();
    }
    
    public static void resetSampleData(){
    	Bootstrap.deleteAllData();
    	Bootstrap.createUserAndProjectData();
    	flash.put("success", "Sample data were deleted and re-created!");
    	Security.logout();
    }
}
