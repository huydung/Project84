package models;

import play.*;
import play.data.validation.Required;
import play.db.jpa.*;
import play.i18n.Messages;

import javax.persistence.*;

import com.huydung.helpers.ActionResult;

import models.enums.ApprovalStatus;
import models.enums.Role;
import models.items.BasicItem;

import java.util.*;

@Entity
public class Project extends BasicItem {
	@Required
    public String name;
	
    public String description;    
    
    public Date deadline;
    
    public Boolean needTrackTime = true;
    public Boolean needClients = true;
    public Boolean needMembers = true;
    public Boolean needFiles = true;
    
    @ManyToOne
    public ProjectTemplate fromTemplate;
    
    @OneToMany(mappedBy = "project")
    public List<Membership> memberships;
    
    @Override
    public String toString(){
    	
    	return name;
    }   
    
    public Project setCreator(User user){
    	Membership m = Membership.findByProjectAndUser(this, user);
    	if( m == null ){
    		m = new Membership(this, user);
    		m.status = ApprovalStatus.ACCEPTED.getName();
    	}
    	m.addRole(Role.ADMIN);
		m.save();
		return this;
    }
    
    public ActionResult addMember(String userEmail){
    	User user = User.findByEmail(userEmail);
    	Membership m = null;
    	//If an account existed with the provided email, Create a Membership for her 
    	if( user != null ){
    		m = Membership.findByProjectAndUser(this, user);
    		//Only add new Membership
    		if( m == null ){
    			m = new Membership(this, user);
    			m.status = ApprovalStatus.UNREAD.getName();
    			m.validateAndSave();
    			return new ActionResult(true, 
    					Messages.get("membership.created", user.nickName, user.email));
    		}else{
    			ApprovalStatus status = m.getApprovalStatus();
    			if( status == ApprovalStatus.ACCEPTED ){
    				return new ActionResult(false, 
    					Messages.get("membership.alreadyAccepted", user.nickName, user.email));
    			}else if( status == ApprovalStatus.DENIED ){
    				//Delete the membership so the user can invite again
    				m.delete();
    				return new ActionResult(false, 
    					Messages.get("membership.alreadyDenied", user.nickName, user.nickName));
    			}else{
    				return new ActionResult(false, 
    					Messages.get("membership.alreadyInvited", user.nickName, user.nickName), true);
    			}
    		}
    	} 
    	//If an account is not existed with the provided email, 
    	//Create a Membership for her and send out invitation
    	else {
    		m = new Membership(this, userEmail);
    		m.status = ApprovalStatus.WAITING_INVITE.getName();
    		if( m.validateAndSave() ){
    			return new ActionResult(true, 
    				Messages.get("membership.invited", userEmail, userEmail));
    		}else{
    			return new ActionResult(false, "Fatal Error In Project.addmember");
    		}
    	}
    }
}
