package controllers;

import play.*;
import play.data.validation.Required;
import play.data.validation.Validation;
import play.db.jpa.JPA;
import play.i18n.Lang;
import play.i18n.Messages;
import play.libs.WS;
import play.libs.XPath;
import play.mvc.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.text.Collator;
import java.util.*;

import org.apache.commons.io.IOUtils;
import org.hibernate.Session;
import org.w3c.dom.*;

import com.google.gson.JsonElement;
import com.sun.org.apache.xpath.internal.res.XPATHErrorResources;

import models.*;
import models.enums.ApprovalStatus;

public class Application extends Controller {
	
	@Before(unless={"homepage","rpx","switchLanguage","inviteResponseFromEmail","devLogin"})
    static void setConnectedUser() {
        if(Security.isLoggedIn()) {
            User user = User.findById(Long.parseLong(Security.getConnectedUserId()));
            //If the cookie exist but user has been deleted from database
            if(user == null){
            	Security.logout();
            	homepage();
            }
            renderArgs.put("loggedin", user);
        }else{
        	Logger.debug("User is connected");
        	Application.homepage();
        }
    }
	
	//@Get("/home")
    public static void homepage() {
    	if( Security.isLoggedIn() ){
    		Application.app();
    	}
        render();
    }
    
    //@Get("/languages")
    public static void switchLanguage(String code, String destination){
    	if( code.equals("en") || code.equals("vi") ){
    		Lang.change(code);
    	}
    	redirect(destination);
    }
    
    //@Get("/")
    public static void app(){
    	User user = renderArgs.get("loggedin", User.class);
    
		List<Membership> memberships = Membership.find("user = ? AND deleted = FALSE", user).fetch();
    	if( memberships == null || memberships.size() == 0 ){
    		flash.keep();
    		Projects.create();
    	}else{
    		flash.keep();
    		Projects.overview();
    	}
    	
    }
    
    //@Post("/rpx")
    public static void rpx(){    	
    	try{
    		String token = params.get("token");
	    	User user = User.getFromRpx(token);
	    	if(user != null){
	    		Security.setConnectedUser(user.id);
	    		
	    		if( user.hasProfile ){
	    			Application.app();
	    		}else{
	    			flash.put("success", Messages.get("success.oauth", user.provider));
		    		flash.keep();
		    		Users.profile(user.id);	    			
	    		}
	    	}else{
	    		flash.put("error", Messages.get("error.oauth"));
	    		flash.keep();
	    		Application.homepage();
	    	}
    	}catch(Exception e){
    		renderText("Error get JSON response: "+ e.getMessage());
    	}
    }
    
    public static void devLogin(Long id){
    	User user = User.findById(id);
    	Security.setConnectedUser(id);
    	if( user.hasProfile ){
			Application.app();
		}else{
			flash.put("success", Messages.get("success.oauth", user.provider));
    		flash.keep();
    		Users.profile(user.id);	    			
		}
    }
    
    public static void inviteResponseFromEmail(
			@Required Long id, @Required String reply,
			@Required String inviteKey		
		){
    	if(Validation.hasErrors()){
    		AppController.displayValidationMessage();
    	}else{
			Membership m = Membership.findById(id);
			if( m == null ){ 
				AppController.notFound("Membership", id); 
			}else {
				
				if( !m.inviteKey.equals(inviteKey)){
					flash.put("error", Messages.get("invitation.wrongKey"));
				} else {
					if( reply.equals("accept") ){
						session.put("invitationId", id);
						flash.put("info", Messages.get("invitation.pleaseLogin"));
					} else {
						//user has denied to join
						m.deny();
						flash.put("success", Messages.get("invitation.denied", 
								m.project.creator.fullName,
								m.project.name));						
					}
				}							
			}
			Application.homepage();
    	}
	}
    
    public static void getLatLang(@Required String address){
    	if(Validation.hasErrors()){
    		notFound();
    	}
    	String url = "?address=" + address;
    	Document xml = WS.url("https://maps.googleapis.com/maps/api/geocode/xml")
    		.setParameter("address", address).post().getXml();
    	Node response = XPath.selectNode("/GeocodeResponse", xml); 
    	String status = XPath.selectText("status", response);
    	if( status == "OK" ){
    		Map<String, String> latLng = new HashMap<String, String>();
    		latLng.put("lat", XPath.selectText("result/geometry/location/lat", response));
    		latLng.put("lng", XPath.selectText("result/geometry/location/lng", response));
    		renderJSON(latLng);
    	}else{
    		notFound();
    	}
    }
    /*
    public static void upload(String qqfile) {
       if(request.isNew) {
		       FileOutputStream moveTo = null;
		
		       Logger.info("Name of the file %s", qqfile);
		       // Another way I used to grab the name of the file
		       String filename = request.headers.get("x-file-name").value();
	       
		       Logger.info("Absolute on where to send %s", Play.getFile("").getAbsolutePath() + File.separator + "uploads" + File.separator);
	       try {
	           InputStream data = request.body;
	           moveTo = new FileOutputStream(new File(Play.getFile("").getAbsolutePath()) + File.separator + "uploads" + File.separator + filename );
	           IOUtils.copy(data, moveTo);           
	       } catch(Exception ex) {
	           renderJSON("{success: false}");
	       }     
       }
   
       renderJSON("{success: true}");
   }
   */
}