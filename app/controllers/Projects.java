package controllers;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.varia.DenyAllFilter;

import com.huydung.helpers.ActionResult;

import models.Activity;
import models.Membership;
import models.Project;
import models.User;
import models.enums.ActivityType;
import models.enums.ApprovalStatus;
import models.enums.DoneStatus;
import models.enums.Role;
import models.templates.ProjectTemplate;
import play.data.binding.As;
import play.data.validation.Required;
import play.data.validation.Valid;
import play.data.validation.Validation;
import play.i18n.Lang;
import play.i18n.Messages;
import play.mvc.*;
import play.data.binding.*;
import play.data.binding.types.DateBinder;

public class Projects extends AppController { 
	@Before
	static void setActive(){
		renderArgs.put("active", "dashboard");
	}
	
	public static void overview(){
		User user = getLoggedin();
		List<Project> active_projects = Project.findByUser(user);
		List<Project> inactive_projects = null;
		if(active_projects != null){
			inactive_projects = new ArrayList<Project>();
			for(Iterator<Project> ite = active_projects.iterator(); ite.hasNext();){
				Project p= ite.next();
				if( p.getStatus() != DoneStatus.ONGOING ){
					inactive_projects.add(p);
					ite.remove();
				}
			}
		} else {
			create();
		}
		
		render(active_projects, inactive_projects);
	}
	
	public static void dashboard(@Required Long project_id){
		if( getActiveMembership() == null ){
			error(403, "Access Denied");
		}
		if(Validation.hasErrors()){
			Application.homepage();
		}else{
			render();
		}		
	}
		
    public static void create(){
    	Project project = new Project();
    	List<ProjectTemplate> templates = ProjectTemplate.getTemplates(getLoggedin());
    	render(project, templates);
    }
	
	//@Post("/projects/create")
    public static void doCreate(Project project){
    	User user = getLoggedin();
    	if( params.get("project.deadline") == "" ){
    		project.deadline = null;
    	}
    	
    	Validation.valid("Project", project);
    	if( Validation.hasErrors() ){
    		displayValidationMessage();
    		List<ProjectTemplate> templates = ProjectTemplate.getTemplates(user);
    		render("projects/create.html", project, templates);
    	}
    	  
    	
    	ActionResult res;
    	res = project.saveAndGetResult(user);
    	
    	if( !res.isSuccess() ){
    		displayError(res.getMessage(), "save-project");
    		List<ProjectTemplate> templates = ProjectTemplate.getTemplates(user);
    		render("projects/create.html", project, templates);
    	}else{
    		project.buildRolePermissions();
    	}
    	
    	res = project.assignCreator(user, null);
    	if( !res.isSuccess() ){
    		displayError(res.getMessage(), "set-creator-when-save-project");
    		List<ProjectTemplate> templates = ProjectTemplate.getTemplates(user);
    		render("projects/create.html", project, templates);
    	}    	
    	
    	if( !res.isSuccess() ){
    		displayWarning(res.getMessage(), "save-activity-when-create-project");
    	}
    	
    	//Projects.structure( project.id );
    	
    	dashboard(project.id);
    }
	
	public static void structure(Long project_id){
		if(getActiveProject() != null){
			
		}else{
			notFound("Project", project_id);
		}
	}

}
