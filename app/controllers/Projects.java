package controllers;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

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
	
	//@Get("/projects")
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
	
	//@Get("/projects/{id}")
	public static void dashboard(@Required Long id){
		if(Validation.hasErrors()){
			Application.homepage();
		}else{
			Project project = Project.findById(id);
			render(project);
		}		
	}
		
	//@Get("/projects/create")
    public static void create(){
    	Project project = new Project();
    	List<ProjectTemplate> templates = ProjectTemplate.getTemplates(getLoggedin());
    	render(project, templates);
    }
	
	//@Post("/projects/create")
    public static void save(Project project){
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
    	res = project.saveAndGetResult();    	
    	if( !res.isSuccess() ){
    		displayError(res.getMessage(), "save-project");
    		List<ProjectTemplate> templates = ProjectTemplate.getTemplates(user);
    		render("projects/create.html", project, templates);
    	}
    	
    	res = project.assignCreator(user, null);
    	if( !res.isSuccess() ){
    		displayError(res.getMessage(), "set-creator-when-save-project");
    		List<ProjectTemplate> templates = ProjectTemplate.getTemplates(user);
    		render("projects/create.html", project, templates);
    	}
    	
    	res = Activity.track("projects.created", project, ActivityType.CHANGE, user);
    	if( !res.isSuccess() ){
    		displayWarning(res.getMessage(), "save-activity-when-create-project");
    	}
    	
    	//Projects.structure( project.id );
    	
    	dashboard(project.id);
    }
	
	//@Get("/projects/{id}/structure")
	public static void structure(Long id){
		Project project = Project.findById(id);
		if(project != null){
			
		}else{
			error(404, Messages.get("error.notFound", "Project", id));
		}
	}

}
