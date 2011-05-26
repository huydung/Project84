package models;

import play.*;
import play.data.validation.Email;
import play.data.validation.InPast;
import play.data.validation.MaxSize;
import play.data.validation.MinSize;
import play.data.validation.Required;

import play.db.jpa.*;
import play.libs.WS;
import play.libs.XPath;

import javax.persistence.*;

import models.templates.ProjectTemplate;

import org.w3c.dom.Document;
import org.w3c.dom.Node;


import java.util.*;

@Entity
public class User extends Model {
	
	/** Attributes **/
	@Required	
    public String fullName; 
	
    @Required
    @MaxSize(15)
    public String nickName;
    
    @Required
    public String identifier;
    
    @Required
    @Email
    public String email;
    
    public Date lastLoggedIn = new Date();
    
    @Required
    @MinSize(8)
    @MaxSize(15)
    public String mobile;
    
    @Required
    public String timeZone = "Asia/Ho_Chi_Minh";
    
    @Required
    public String dateFormat = "dd MMM, YYYY";
    
    @Transient
    public String provider;
    
    @Transient
    public Boolean hasProfile = true;
    
    /** Relationships **/

    
    public String toString(){
    	return fullName;
    }
    
    public static User getFromRpx(String token){
    	try{    	
    		Document xml = WS.url("https://rpxnow.com/api/v2/auth_info")
    		.setParameter("apiKey", "dac2fcdb0d5a54ecbb6a32b50c16f6de22b036cc")
    		.setParameter("token", token)
    		.setParameter("format", "xml")
    		.get().getXml();
    		Node profile = XPath.selectNode("/rsp/profile", xml); 
    		
    		String identifier = XPath.selectText("identifier", profile);
    		User user = User.find("identifier = ?", identifier).first();
    		if( user == null ){
    			user = new User();
    			user.identifier = identifier;
    			user.provider = XPath.selectText("providerName", profile);    			
    			user.email = XPath.selectText("email", profile);	    		
    			user.fullName = XPath.selectText("name/formatted", profile);
    			user.nickName = XPath.selectText("preferredUsername", profile);
    			user.hasProfile = false;
    			//user.dateFormat = "";
    			user.save();
    		}else{
    			user.lastLoggedIn = new Date();
    			user.save();
    		}
    		return user;
    	}catch(Exception e){
    		return null;
    	}
    }
    
    public static User findByEmail(String email){
    	return User.find("email", email).first();
    }
}
