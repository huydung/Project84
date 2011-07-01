package controllers;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.mail.internet.PreencodedMimeBodyPart;

import notifiers.Emails;

import org.apache.commons.lang.StringUtils;

import com.huydung.helpers.ActionResult;

import play.data.validation.Required;
import play.data.validation.Valid;
import play.data.validation.Validation;
import play.i18n.Messages;
import play.mvc.Before;
import play.mvc.With;

import models.Activity;
import models.Membership;
import models.Project;
import models.User;
import models.enums.ApprovalStatus;
import models.enums.PermissionKey;
import models.enums.Role;

@With(Authorization.class)
public class Memberships extends AppController {
	@Before
	static void setActive(){
		renderArgs.put("active", "people");
	}
	
	public static void show(@Required Long membership_id){
		render();
	}
	
	public static void dashboard(Long project_id){
		Membership _m = getActiveMembership();		
		Project _project = getActiveProject();
		if( _m == null ){
			error(403, "Access Denied");
		}
		
		//Get list of all memberships and separate them into Members and Clients
		if( _project != null && (_project.needMembers )){
			List<Membership> members = Membership.findByProject(_project, 0);
			List<Membership> clients = new ArrayList<Membership>();
			if(members != null){
				for(Iterator<Membership> ite = members.iterator(); ite.hasNext();){
					Membership m = ite.next();
					if( m.isClient() ){
						clients.add(m);
						ite.remove();
					}
				}
			}
			render(members, clients);			
		}
	}
	
	public static void create(Long project_id){
		render("memberships/form_add.html");
	}
	
	public static void doCreate(
			@Required String email, @Required String member_title,
			@Required Long project_id, @Required Boolean isClient){	
		if( Validation.hasErrors() ){
			displayValidationMessage();
			params.flash();			
			dashboard(project_id);
		}
		Project p = getActiveProject();
		if( p == null ){
			displayValidationMessage();
			params.flash();
			dashboard(project_id);
		}
		ActionResult r = p.addMember(email, member_title, isClient, getLoggedin());
		if( !r.isSuccess() ){
			params.flash();
			displayError(r.getMessage(), "add-member");			
		}else{
			flash.put("success", Messages.get("labels.emailSent", email));
		}
		dashboard(project_id);
	}
	
	public static void edit(@Required Long id, @Required Long project_id){
		Membership membership = Membership.findById(id);
		if( membership == null ){notFound("Membership", id);}
		render("memberships/form_edit.html", membership);
	}
	
	public static void doEdit(
		@Required Long id, @Required Long project_id, Role[] roles, @Required String title
	){
		if( Validation.hasErrors() ){
			displayValidationMessage();
			dashboard(project_id);
		}
		Membership m = Membership.findById(id);
		if( m == null ){notFound("Membership", id);}
		
		m.title = title;
		if( getActiveProject().allow(
				getActiveMembership(), PermissionKey.EDIT_USERS_PERMISSIONS) ){
			m.setRoles(roles);
		}			
		m.update(getLoggedin());
		flash.put("success", "Membership has been updated");	
		dashboard(project_id);		
	}
	
	public static void delete(@Required Long id, @Required Long project_id){
		if( Validation.hasErrors() ){
			displayValidationMessage();			
		}else{
			Membership m = Membership.findById(id);
			if( m == null ){notFound("Membership", id);}
			
			m.delete(getLoggedin());
			flash.put("success", "Membership deleted");			
		}		
		dashboard(project_id);
	}
	
	public static void sendInvite(@Required Long m_id, @Required Long project_id, @Required String email, Boolean isClient){
		Emails.sendInvitationToMember(email, getLoggedin().id, project_id, m_id, isClient);
		flash.put("success", Messages.get("labels.emailSent", email));
		dashboard(project_id);
	}
	
	public static void saveInvitationResponse(String[] responses ){
		if( responses != null && responses.length > 0 ){
			for(String s : responses){
				String[] results = s.split("-");
				Long id = Long.parseLong(results[0], 10);
				Membership m = Membership.findById(id);
				if( m == null ){notFound("Membership", id);}
				
				if( results[1].equals("true") ){
					m.accept(getLoggedin());
				}else{
					m.deny(getLoggedin());
				}				
			}
		}
		if( session.contains("destination") ){
			String path = session.get("destination");
			session.remove("destination");
			redirect(path);
		}else{
			Application.app();
		}
	}
	
	public static void invitations(){
		render();
	}
}
