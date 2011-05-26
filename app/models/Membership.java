package models;

import play.*;
import play.data.validation.Check;
import play.data.validation.CheckWith;
import play.data.validation.Required;
import play.db.jpa.*;
import play.i18n.Messages;
import play.mvc.Router;
import play.templates.JavaExtensions;

import javax.persistence.*;

import org.joda.time.JodaTimePermission;

import com.huydung.utils.Link;

import models.enums.ApprovalStatus;
import models.enums.Role;

import java.util.*;

@Entity
public class Membership extends Model implements IWidget, IWidgetItem {
	
	@Required
	public String title = Messages.get("project.member");
    
	@CheckWith(MustHaveUserOrEmail.class)
	public String userEmail;	

	@ManyToOne(fetch = FetchType.EAGER)
	public User user;
	
	@Required
	@ManyToOne
	public Project project;
	
	@Required
    public String roleNames = Role.MEMBER.getName();
    
    @Required
    @Enumerated(EnumType.STRING)
    public ApprovalStatus status = ApprovalStatus.ACCEPTED;
    
    public Membership(Project project, String userEmail) {
		super();
		this.userEmail = userEmail;
		this.project = project;
	}
    
    public Membership(Project project, User user) {
		super();
		this.user = user;
		this.project = project;

	}

	public Membership(Project project, User user, String title) {
		this(project, user);
		this.title = title;
	}
	
	static class MustHaveUserOrEmail extends Check {
		public boolean isSatisfied(Object m, Object userEmail) {
    	  Membership ms = (Membership)m;
    	  if( ms.user == null && userEmail == null ){
    		  return false;
    	  }
    	  return true;
		}	
	}
    
    public List<Role> getRoles(){
    	if( this.roleNames != null && this.roleNames.length() > 0 ){
	    	ArrayList<Role> results = new ArrayList<Role>();
	    	String[] _roles = this.roleNames.replace(" ", "").split(",");
	    	for( String r : _roles){
	    		Role role = Role.parse(r);
	    		if(role != null){
	    			results.add(role);
	    		}
	    	};
	    	return results;
    	};
    	return null;
    }
    
    public boolean hasRole(Role role){
    	if( roleNames.startsWith(role.getName()) ){
    		return true;
    	}else if( roleNames.endsWith(role.getName()) ){
    		return true;
    	}else{
    		return roleNames.contains("," + role.getName() + ",");
    	}
    }
    
    public void setRoles(List<Role> roles){

    	this.roleNames = "";
    	for(Role role : roles){
    		this.roleNames += role.getName() + ",";
    	}
    	if(this.roleNames.length() > 0){
    		this.roleNames.substring(this.roleNames.length() - 1);
    	} 
    }
    
    public String getEmail(){
    	if( user != null ){
    		return user.email;
    	}else{
    		return userEmail;
    	}
    }
    
    public boolean isInvitation(){
    	if( user != null && status == ApprovalStatus.ACCEPTED ){
    		return false;
    	}
    	return true;
    }
    
    
    public void addRole(Role role){
    	if( !hasRole(role) ){
    		roleNames += "," + role.getName();
    	}
    }
    
    public static Membership findByProjectAndUser(Project project, User user){
    	return Membership.find("project = ? AND user = ?", project, user).first();
    }
    
    public static List<Membership> findByUserEmailAndStatus(String email, ApprovalStatus status){
    	return Membership.find("userEmail = ? AND status = ?", email, status).fetch();
    }
    
    public static List<Membership> findByUser(User user){
    	return Membership.find("user", user).fetch();
    }
    
    public static List<Membership> findByProject(Project project, int limit){
    	if( limit > 0 ){
    		return Membership.find("project = ? ORDER BY title", project).fetch(limit);
    	}else{
    		return Membership.find("project = ?", project).fetch();
    	}
    }

	@Override
	public String getName() {		
		return Messages.get("project.people");
	}
	@Override
	public String getSubName() {		
		return Messages.get("user.lastLoggedIn");
	}

	@Override
	public String getHtmlClass() {		
		return "people";
	}

	@Override
	public Link getFirstLink() {		
		Map<String, Object> args = new HashMap<String, Object>();
		args.put("project_id", this.project.id);
		return new Link( 
				Messages.get("labels.viewAll"),
				Router.getFullUrl("Memberships.dashboard", args),
				"icon-link-list"
		);
	}

	@Override
	public Link getLastLink() {
		Map<String, Object> args = new HashMap<String, Object>();
		args.put("project_id", this.project.id);
		return new Link( 
				Messages.get("project.addMember"),
				Router.getFullUrl("Memberships.create", args),
				"icon-link-add"
		);
	}

	@Override
	public List getItems(Long project_id) {
		List<Membership> memberships = Membership.findByProject(this.project, 7);
		
		for(Iterator<Membership> ite = memberships.iterator(); ite.hasNext(); ){
			if( ite.next().isInvitation() ){
				ite.remove();
			};
		}
		return memberships;
	}

	@Override
	public String getHtml() {
		return null;
	}

	@Override
	public String getSubInfo() {		
		if(this.user != null){
			return JavaExtensions.since(this.user.lastLoggedIn);
		}else{
			return "[...]";
		}		
	}

	@Override
	public Link getInfo() {
		Map<String, Object> args = new HashMap<String, Object>();
		args.put("id", this.id);
		
		return new Link(
			this.title + "|" + this.user.fullName,
			Router.getFullUrl("Membership.show", args)
		);		
	}

}
