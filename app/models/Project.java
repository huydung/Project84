package models;

import play.*;
import play.data.binding.As;
import play.data.validation.Required;
import play.db.jpa.*;
import play.i18n.Messages;

import groovy.swing.factory.WidgetFactory;

import javax.persistence.*;

import com.huydung.helpers.ActionResult;
import com.huydung.utils.MiscUtil;

import models.enums.ActivityType;
import models.enums.ApprovalStatus;
import models.enums.DoneStatus;
import models.enums.PermissionKey;
import models.enums.Role;
import models.templates.ListTemplate;
import models.templates.ProjectTemplate;

import java.util.*;

@Entity
public class Project extends BasicItem {
	
    public String description;    

    public Date deadline;
    
    public Boolean needMembers = false;
    //public Boolean needClients = false;
    
    public String doneStatus = DoneStatus.ONGOING.getName();
    
    public Project() {
		super();
		this.type = "project";
		this.created = new Date();
	}	
     
    @Required
    @ManyToOne()
    public ProjectTemplate fromTemplate;
    
    
    @OneToMany(mappedBy = "project")
    public List<Membership> memberships;
    
    @OneToMany(mappedBy = "project", fetch = FetchType.EAGER)
    public List<RolePermission> rolePermissions;
    
    @OneToMany(mappedBy = "project", fetch = FetchType.LAZY)
    public List<Listing> listings;   
     
    
    @Transient
    private List<IWidget> widgets = null;
    
    @Override
    public String toString(){ return name; }   
    
    public DoneStatus getStatus(){
    	return DoneStatus.parse(doneStatus);
    }
    
    public void setStatus(DoneStatus d){
    	doneStatus = d.getName();
    }
    
    /**
     * This method MUST be called AFTER save() the project
     */
    public void buildRolePermissions(){
    	for(Role role : Role.values()){
    		RolePermission rp = new RolePermission(this, role, PermissionKey.getDefaultPermissions(role));
	    	rp.save();
    	}    	
    }
    
    public ActionResult assignCreator(User user, String title){
    	Membership m = Membership.findByProjectAndUser(this, user);
    	if( m == null ){
    		m = new Membership(this, user, title == null ? Messages.get("project.manager") : title);    		
    	}
    	m.status = ApprovalStatus.ACCEPTED;
    	m.addRole(Role.ADMIN);
    	if( m.validateAndSave() && m.id > 0 ){
    		return new ActionResult(true);
    	}else{
    		return new ActionResult(false, 
    			Messages.get("error.project.setCreator"));
    	}
    }
    
    public boolean allow(Membership m, PermissionKey key){
    	if( m == null ){ return false; }
    	if( key == null ){ return false; }
    	
    	MiscUtil.ConsoleLog("Checking if user "+m.getEmail()+" can "+key.toString()+" with Project "+ this.name);
    	List<Role> roles = m.getRoles();
		for(Role role : roles){
			for(RolePermission rp : rolePermissions){
    			if( rp.check(role, key) ){
    				return true;
	    		}
	    	}
		}    	
		return false;
    }

    public ActionResult saveAndGetResult(User actor){
    	if( this.validateAndSave() && this.id > 0 ){
    		Activity.track(Messages.get("projects.created", actor.fullName), this, ActivityType.CHANGE, actor);
    		return new ActionResult(true);
    	}else{
    		return new ActionResult(false, 
    			Messages.get("error.project.save"));
    	}
    }
    
    public ActionResult addMember(String userEmail, String title, boolean isClient, User actor){
    	User user = User.findByEmail(userEmail);
    	Membership m = null;
    	//If an account existed with the provided email, Create a Membership for her 
    	if( user != null ){
    		m = Membership.findByProjectAndUser(this, user);
    		
    		if( m == null ){
    			m = new Membership(this, user, title);
    			if( isClient ){ m.setRole(Role.CLIENT); }
    			m.status = ApprovalStatus.WAITING_INVITE;
    			m.inviteKey = m.generateInvitationKey();
    			m.userEmail = userEmail;
    			boolean saved = m.validateAndSave();
    			if(saved){
    				Activity.track(Messages.get(
    					"membership.created.log", actor.fullName, userEmail), this, ActivityType.ITEM, actor);
    				
	    			return new ActionResult(true, 
	    					Messages.get("membership.created", user.nickName, user.email),
	    					m);
    			}else{
    				return new ActionResult(false, "Fatal Error In Project.addmember");
    			}
    		}else{
    			if( m.status == ApprovalStatus.ACCEPTED ){
    				return new ActionResult(false, 
    					Messages.get("membership.alreadyAccepted", user.nickName, user.email));
    			}else if( m.status == ApprovalStatus.DENIED ){
    				//Delete the membership so the user can invite again
    				m.delete();
    				return new ActionResult(false, 
    					Messages.get("membership.alreadyDenied", user.nickName, user.nickName));
    			}else{
    				return new ActionResult(false, 
    					Messages.get("membership.alreadyInvited", user.nickName), true);
    			}
    		}
    	} 
    	//If an account is not existed with the provided email, 
    	//Create a Membership for her and send out invitation
    	else {
    		m = Membership.findByProjectAndUserEmail(this, userEmail);
    		if( m != null ){
    			return new ActionResult(false, 
    					Messages.get("membership.alreadyInvited", userEmail), true);
    		}else{
    			m = new Membership(this, userEmail);
    			if( isClient ){ m.setRole(Role.CLIENT); }
        		m.status = ApprovalStatus.WAITING_INVITE;
        		m.inviteKey = m.generateInvitationKey();
        		if( m.validateAndSave() ){
        			
        			Activity.track(Messages.get(
        					"membership.created.log", actor.fullName, userEmail), this, ActivityType.ITEM, actor);
        				
        			return new ActionResult(true, 
        				Messages.get("membership.invited", userEmail, userEmail),
        				m);
        		}else{
        			return new ActionResult(false, "Fatal Error In Project.addmember");
        		}
    		}    		
    	}
    }
    
    public static long countByUser(User user, ApprovalStatus status, Role role){
    	if( role == null ){
    		return Membership.count("user = ? AND status = ?", 
    				user, status);
    	}else{
    		return Membership.count("user = ? AND status = ? AND roleNames LIKE '%?%'", 
    				user, status, role.toString());
    	}
    }
    
    public static List<Project> findByUser(User user){    	
    	return Project.find("SELECT DISTINCT p FROM Project p LEFT JOIN p.memberships m WHERE m.user = ? AND m.deleted = 0", user).fetch();
    }
    
    public List<IWidget> getWidgets(User user){
    	if( widgets == null ){
    		widgets = new ArrayList<IWidget>();
    		if( this.needMembers ){
    			widgets.add(new Membership(this, user));
    		}
    		widgets.add(new Activity(this));
    	}
    	return widgets;
    }
    
    public void copyFromTemplate(ProjectTemplate pt){
    	this.fromTemplate = pt;
    	this.needMembers = pt.needMembers;
    	this.save();
    	for( ListTemplate lt : pt.getListTemplates() ){    		
    		Listing l = Listing.createFromTemplate(lt, this);
    		l.save();
    	}  	
    	
    }

}
