package controllers;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.mail.internet.PreencodedMimeBodyPart;

import com.huydung.helpers.ActionResult;

import play.data.validation.Required;
import play.data.validation.Valid;
import play.data.validation.Validation;

import models.Membership;
import models.Project;
import models.enums.Role;

public class Memberships extends AppController {
	
	public static void dashboard(Long project_id){
		Project project = Project.findById(project_id);
		if( project != null && (project.needMembers || project.needClients)){
			List<Membership> members = Membership.findByProject(project, 0);
			if( project.needClients ){
				List<Membership> clients = new ArrayList<Membership>();
				if(members != null){
					for(Iterator<Membership> ite = members.iterator(); ite.hasNext();){
						Membership m = ite.next();
						if( m.hasRole(Role.CLIENT) ){
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
	
	public static void save(
			@Required String email, @Required String member_title,
			@Required Long project_id){		
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
		ActionResult r = p.addMember(email, member_title);
		if( !r.isSuccess() ){
			params.flash();
			displayError(r.getMessage(), "add-member");
		}
		dashboard(project_id);
	}
	
	public static void edit(@Required Long id, @Required Long project_id){
		
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
	
}
