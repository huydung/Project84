package models;

import play.*;
import play.data.validation.Check;
import play.data.validation.CheckWith;
import play.data.validation.Required;
import play.db.jpa.*;

import javax.persistence.*;

import models.enums.ApprovalStatus;
import models.enums.Role;

import java.util.*;

@Entity
public class Membership extends Model {
    
	@CheckWith(MustHaveUserOrEmail.class)
	public String userEmail;	

	@ManyToOne
	@CheckWith(MustHaveUserOrEmail.class)
	public User user;
	
	@Required
	@ManyToOne
	public Project project;
	
	@Required
    public String roles = Role.MEMBER.getName();
    
    @Required
    public String status = ApprovalStatus.ACCEPTED.getName();
    
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
	
	static class MustHaveUserOrEmail extends Check {
		public boolean isSatisfied(Object m, Object userEmail) {
    	  Membership ms = (Membership)m;
    	  if( ms.user == null && userEmail == null ){
    		  return false;
    	  }
    	  return true;
		}	
	}

	public ApprovalStatus getApprovalStatus(){
    	return ApprovalStatus.parse(status);
    }
    
    public List<Role> getRoles(){
    	ArrayList<Role> results = new ArrayList<Role>();
    	String[] _roles = this.roles.replace(" ", "").split(",");
    	for( String r : _roles){
    		Role role = Role.parse(r);
    		if(role != null){
    			results.add(role);
    		}
    	}
    	return results;
    }
    
    public boolean hasRole(Role role){
    	if( roles.startsWith(role.getName()) ){
    		return true;
    	}else if( roles.endsWith(role.getName()) ){
    		return true;
    	}else{
    		return roles.contains("," + role.getName() + ",");
    	}
    }
    
    public void setRoles(List<Role> roles){

    	this.roles = "";
    	for(Role role : roles){
    		this.roles += role.getName() + ",";
    	}
    	if(this.roles.length() > 0){
    		this.roles.substring(this.roles.length() - 1);
    	} 

    }
    
    public void addRole(Role role){
    	if( !hasRole(role) ){
    		roles += "," + role.getName();
    	}
    }
    
    public static Membership findByProjectAndUser(Project project, User user){
    	return Membership.find("project = ? AND user = ?", project, user).first();
    }
    
    public static List<Membership> findByUserEmailAndStatus(String email, ApprovalStatus status){
    	return Membership.find("userEmail = ? AND status = ?", email, status.getName()).fetch();
    }
    
    public static List<Membership> findByUser(User user){
    	return Membership.find("user", user).fetch();
    }
}
