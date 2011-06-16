package controllers;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.varia.DenyAllFilter;

import com.huydung.helpers.ActionResult;
import com.huydung.utils.ItemField;
import com.huydung.utils.PermConfig;

import models.Activity;
import models.Listing;
import models.Membership;
import models.Project;
import models.RolePermission;
import models.User;
import models.enums.ActivityType;
import models.enums.ApprovalStatus;
import models.enums.DoneStatus;
import models.enums.PermissionKey;
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

@With(Authorization.class)
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
    	List<ProjectTemplate> templates = ProjectTemplate.getTemplates(user);
    	if( params.get("project.deadline") == "" ){
    		project.deadline = null;
    	}
    	
    	Validation.valid("Project", project);
    	if( Validation.hasErrors() ){
    		displayValidationMessage();
    		render("projects/create.html", project, templates);
    	}
    	  
    	
    	ActionResult res;
    	res = project.saveAndGetResult(user);
    	
    	if( !res.isSuccess() ){
    		displayError(res.getMessage(), "save-project");
    		render("projects/create.html", project, templates);
    	}
    	
    	res = project.assignCreator(user, null);
    	if( !res.isSuccess() ){
    		displayError(res.getMessage(), "set-creator-when-save-project");    		
    		render("projects/create.html", project, templates);
    	}    	
    	project.copyFromTemplate(project.fromTemplate);
    	
    	if( !res.isSuccess() ){
    		displayWarning(res.getMessage(), "save-activity-when-create-project");
    	}
    	
    	//Projects.structure( project.id );    	
    	dashboard(project.id);
    }
	
	public static void structure(Long project_id){
		Project p = getActiveProject();
		renderArgs.put("active", "settings");
		if(p != null){
			List<Listing> listings = p.listings;
			List<ItemField> item_fields = models.Item.getItemFields();
			render(listings, item_fields);
		}else{
			notFound("Project", project_id);
		}
	}
	
	public static void permissions(Long project_id){
		Project p = getActiveProject();
		if( p == null ){ error(400, "Bad Request"); }
		LinkedHashMap<String, List<PermConfig>> perms = 
			PermissionKey.getPermissions(p);
		List<Role> roles = Role.getRoles();
		renderArgs.put("active", "people");
		render( perms, roles );
	}
	
	public static void savePermissions(Long project_id){
		Project p = getActiveProject();
		if( p == null ){ error(400, "Bad Request"); }
		for( Role r : Role.values() ){
			String key = "permConfigs[" + r.toString() + "][]";
			String[] perms = params.getAll(key);
			String permisisons = StringUtils.join(perms, ",");
			for( RolePermission rp : p.rolePermissions ){
				if( rp.role == r ){
					rp.permissions = permisisons;
					rp.save();
				}
			}
		}
		flash.put("success", "Permissions for Members of the Project have been saved");
		permissions(project_id);
	}
	
}
