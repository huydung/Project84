package notifiers;

import models.Project;
import models.User;
import play.i18n.Messages;
import play.mvc.Mailer;

public class Emails extends Mailer {
	
	public static void invitationToMember(
			String email, Long user_id, Long project_id, Boolean isClient
		){
		Project project = Project.findById(project_id);
		User from = User.findById(user_id);
		setSubject(Messages.get("members.invite.subject", project.name, from.fullName));
		addRecipient(email);
		String fromFormated = from.fullName + " <" + from.email + ">";
		setFrom(fromFormated);
		setReplyTo(fromFormated);
		send(email, from, project);
	}
	
}
