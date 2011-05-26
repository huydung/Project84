package controllers;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

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
		render();
	}
	
	public static void edit(Long id){
		
	}
	
	public static void delete(Long id){
		
	}
	
}
