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

import models.Membership;
import models.Project;
import models.User;
import models.enums.Role;

public class Memberships extends AppController {
	@Before
	static void setActive(){
		renderArgs.put("active", "people");
	}
	
	public static void dashboard(Long project_id){
		Project project = Project.findById(project_id);
		if( project != null && (project.needMembers || project.needClients)){
			List<Membership> members = Membership.findByProject(project, 0);
			if( project.needClients ){
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
				render(members, clients, project);
			}
			render(members, project);
		}
	}
	
	public static void create(Long project_id){
		Project project = Project.findById(project_id);		
		render("memberships/form_add.html", project);
	}
	
	public static void doCreate(
			@Required String email, @Required String member_title,
			@Required Long project_id, @Required Boolean isClient){		
		if( Validation.hasErrors() ){
			displayValidationMessage();
			params.flash();
			dashboard(project_id);
		}
		Project p = Project.findById(project_id);
		if( p == null ){
			displayValidationMessage();
			params.flash();
			dashboard(project_id);
		}
		ActionResult r = p.addMember(email, member_title, isClient);
		if( !r.isSuccess() ){
			params.flash();
			displayError(r.getMessage(), "add-member");
		}
		Membership m = (Membership)r.getData();
		//send email
		Emails.invitationToMember(email, getLoggedin().id, project_id, m.isClient());
		flash.put("success", Messages.get("labels.emailSent", email));
		dashboard(project_id);
	}
	
	public static void edit(@Required Long id, @Required Long project_id){
		Membership membership = Membership.findById(id);
		if( membership != null ){
			render("memberships/form_edit.html", membership);
		}else{
			error(404, Messages.get("error.notFound", "Membership", id));
		}
	}
	
	public static void doEdit(
		@Required Long id, @Required Long project_id, Role[] roles, @Required String title
	){
		if( Validation.hasErrors() ){
			displayValidationMessage();
			dashboard(project_id);
		}
		Membership m = Membership.findById(id);
		if(m != null){
			m.title = title;
			m.roleNames = StringUtils.join(roles, ",").toLowerCase();
			m.save();
			dashboard(project_id);
		}
	}
	
	public static void delete(@Required Long id, @Required Long project_id){
		if( Validation.hasErrors() ){
			displayValidationMessage();			
		}else{
			Membership m = Membership.findById(id);
			if( m != null ){
				m.deleted = true;
				m.save();
				flash.put("success", "Membership deleted");			
			}
		}
		dashboard(project_id);
	}
	
	public static void sendInvite(@Required Long project_id, @Required String email, Boolean isClient){
		Emails.invitationToMember(email, getLoggedin().id, project_id, isClient);
		flash.put("success", Messages.get("labels.emailSent", email));
		dashboard(project_id);
	}
}
