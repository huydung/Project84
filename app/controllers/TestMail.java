package controllers;

import models.Project;
import models.User;
import play.mvc.Controller;

public class TestMail extends Controller {
	public static void testInvitationMail(
		String email, Long user_id, Long project_id, Boolean isClient
	){
		User from = User.findById(user_id); 
		Project project = Project.findById(project_id);		
		render("Mails/sendInvitationToMember.html", email, from, project, isClient);
	}
}
