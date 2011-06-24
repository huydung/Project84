package models;

import play.*;
import play.data.binding.As;
import play.data.validation.Required;
import play.db.jpa.*;
import play.i18n.Messages;

import javax.persistence.*;

import org.hibernate.annotations.Filter;

import com.huydung.helpers.ActionResult;
import com.huydung.utils.MiscUtil;
import com.huydung.utils.PermConfig;

import models.enums.ActivityType;
import models.enums.ApprovalStatus;
import models.enums.DoneStatus;
import models.enums.PermissionKey;
import models.enums.Role;
import models.templates.ListTemplate;
import models.templates.ProjectTemplate;

import java.util.*;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
public class Project extends BasicItem {

    public String description;    

    public Date deadline;
    
    public Boolean needMembers = true;
    //public Boolean needClients = false;
    
    @Enumerated( EnumType.STRING)
    public DoneStatus status = DoneStatus.ONGOING;
    
    public Project() {
		super();
	}	
     
    @Required
    @ManyToOne()
    public ProjectTemplate fromTemplate;
    
    
    @OneToMany(mappedBy = "project")
    @Filter(name="deleted")
    public List<Membership> memberships;
    
    @OneToMany(mappedBy = "project", fetch = FetchType.EAGER)
    public List<RolePermission> rolePermissions;
    
    @OneToMany(mappedBy = "project", fetch = FetchType.LAZY)
    @OrderBy("ordering ASC")
    @Filter(name="deleted")
    public List<Listing> listings;   
     
    
    @Transient
    private List<IWidget> widgets = null;
    
    @Override
    public String toString(){ return name; }   
    
    /**
     * This method MUST be called AFTER save() the project
     */
    public void buildRolePermissions(){
    	this.save();
    	for(Role role : Role.values()){
    		RolePermission rp = new RolePermission(this, role, PermissionKey.getDefaultPermissions(role));
    		//TODO: For all Listing, build default
	    	rp.save();
    	}       	
    }
    
    public void addListingPermissions(Listing l){
    	if( this.rolePermissions == null ){
    		return;
    	}
    	boolean alreadyHad = true;
    	String str = PermissionKey.LISTING_CONFIG.toString() + "_" + l.id;
    	
    	for( RolePermission rp : this.rolePermissions ){
    		if( !rp.permissions.contains(str) ){
    			alreadyHad = false;
    			break;
    		}
    	}
    	if(!alreadyHad){
    		for( RolePermission rp : this.rolePermissions ){
       			String p = PermissionKey.getDefaultListingPermissions(rp.role, l.id);
       			if( p!= null && p.length() > 0 ){
       				if(rp.permissions != null && rp.permissions.length() > 0){
       					rp.permissions += ",";
       				}
       				rp.permissions += p;
           			rp.save();
       			}       			
        	}
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
    
    public boolean allow(Role r, String key){
		for(RolePermission rp : rolePermissions){
			if( rp.role == r ){
				return rp.check(r, key);
			}
    	} 	
		return false;
    }
    
    public boolean allow(Membership m, String key){
    	if( m == null ){ return false; }
    	if( key == null ){ return false; }    	
    	List<Role> roles = m.getRoles();
		for(Role role : roles){
			if(allow(role, key)){ return true; }
		}    	
		return false;
    }
    
    public boolean allow(Membership m, PermissionKey key){
    	return allow(m, key.toString());
    }
    
    public boolean allow(Membership m, PermissionKey key, Listing l){
    	if( l == null ){ return false; }
    	return allow(m, key.toString() + "_" + l.id);
    }

    public ActionResult createAndGetResult(User actor){
    	if( this.validateAndSave() && this.id > 0 ){
    		Activity.track(Messages.get("projects.created", actor.fullName), this, ActivityType.CHANGE, actor);
    		return new ActionResult(true);
    	}else{
    		return new ActionResult(false, 
    			Messages.get("error.project.save"));
    	}
    }
    

    public Project save(User user){
    	this.save();
    	Activity.track("The project [" + this.name + "] has been updated by [" + user.nickName +"]", this, ActivityType.CHANGE, user);
    	return this;
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
    	return Project.find("SELECT DISTINCT p FROM Project p LEFT JOIN p.memberships m WHERE m.user = ? AND m.deleted = FALSE and p.deleted = FALSE", user).fetch();
    }
    
    public List<IWidget> getWidgets(User user){
    	if( widgets == null ){
    		widgets = new ArrayList<IWidget>();
    		if( this.needMembers ){
    			widgets.add(new Membership(this, user));
    		}
    		widgets.addAll(this.listings);
    		widgets.add(new Activity(this));    		
    	}
    	return widgets;
    }
    
    public void copyFromTemplate(ProjectTemplate pt){
    	buildRolePermissions();
    	refresh();
    	this.fromTemplate = pt;
    	this.needMembers = pt.needMembers;
    	this.save();
    	for( ListTemplate lt : pt.getListTemplates() ){    		
    		addListing(lt, lt.name);    		
    	}  	
    	
    }
    
    public Listing addListing(ListTemplate lt, String name){
    	Listing l = Listing.createFromTemplate(lt, this);
    	l.listingName = name;
    	l.save();
		addListingPermissions(l);
		return l;
    }
    
    @Override
    public Project delete(){    	
    	this.deleted = true;
    	this.save();
    	return this;
    }
    
    public boolean isActive(){
    	return status == DoneStatus.ONGOING;
    }
}
