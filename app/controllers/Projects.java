package controllers;

import java.io.File;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import net.sf.jxls.transformer.XLSTransformer;

import org.apache.commons.codec.EncoderException;
import org.apache.commons.codec.net.URLCodec;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.varia.DenyAllFilter;
import org.apache.poi.ss.usermodel.Workbook;

import com.huydung.helpers.ActionResult;
import com.huydung.utils.ItemField;
import com.huydung.utils.MiscUtil;
import com.huydung.utils.PermConfig;

import models.Activity;
import models.Item;
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
import models.templates.ListTemplate;
import models.templates.ProjectTemplate;
import models.vos.VProject;
import play.Logger;
import play.Play;
import play.data.binding.As;
import play.data.validation.Required;
import play.data.validation.Valid;
import play.data.validation.Validation;
import play.i18n.Lang;
import play.i18n.Messages;
import play.mvc.*;
import play.vfs.VirtualFile;
import play.data.binding.*;
import play.data.binding.types.DateBinder;

@With(Authorization.class)
public class Projects extends AppController { 
	@Before
	static void setActive(){
		renderArgs.put("active", "dashboard");
	}
	
	public static void overview(){
		if(renderArgs.get("_projects") != null || renderArgs.get("_inactive_projects") != null){
			render();
		} else {
			create();
		}		
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
    	List<ProjectTemplate> templates = ProjectTemplate.getTemplates(getLoggedin(), false);
    	render(project, templates);
    }
	
	//@Post("/projects/create")
    public static void doCreate(Project project){
    	User user = getLoggedin();
    	List<ProjectTemplate> templates = ProjectTemplate.getTemplates(user, false);
    	if( params.get("project.deadline") == "" ){
    		project.deadline = null;
    	}
    	
    	Validation.valid("Project", project);
    	if( Validation.hasErrors() ){
    		displayValidationMessage();
    		render("projects/create.html", project, templates);
    	}
    	  
    	
    	ActionResult res;
    	res = project.createAndGetResult(user);
    	
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
    	structure(project.id);
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
	
	public static void edit(@Required Long project_id){
		render();
	}
	
	public static void doEdit(Project project){
		if( params.get("project.deadline") == "" ){
    		project.deadline = null;
    	}
		project.save(getLoggedin());
		flash.put("success", "Project Information has been saved");
		dashboard(project.id);
	}
	
	public static void delete(Long project_id){
		Project p = getActiveProject();
		if( p == null ){ notFound(); }
		p.delete();
		Application.homepage();
	}
	
	public static void templates(Long project_id){
		List<ProjectTemplate> templates = ProjectTemplate.getTemplates(getLoggedin(), true);
		render(templates);
	}
	
	public static void createTemplate(Long project_id, 
			@Required(message="A Project template need at least 1 listing") Long[] listings, 
			@Required(message="Template Name is required") String name){
		if(Validation.hasErrors()){
			displayValidationMessage();
			List<ProjectTemplate> templates = ProjectTemplate.getTemplates(getLoggedin(), true);
			render("projects/templates.html", templates);
		}
		User u = getLoggedin();
		ProjectTemplate pt = ProjectTemplate.createFromProject(
				getActiveProject(), name, listings, u, params);
		pt.save();
		flash.put("success", "Project Template created!");		
		templates(project_id);
	}
	
	public static void export(Long project_id){
		//Prepare data
		Project p = getActiveProject();
		if( p == null ){ notFound("Project", project_id); }
		List<Listing> listings = p.listings;
		List<String> sheetNames = new ArrayList();
		List maps = new ArrayList();
		for(Listing l : listings){
			Map map = new HashMap();
			map.put("project", VProject.createFromProject(p));
			
			sheetNames.add("(" + l.id + ") " + l.listingName);
			
			//l.items = Item.findByListing(l, "", l.sort, 1000);
			map.put("listing", l.listingName);
			map.put("items", Item.findByListing(l, "", l.sort, 1000));
			List<ItemField> selected_fields = l.getItemFields();
			List<ItemField> all_fields = Item.getItemFields();
			List<ItemField> other_fields = new ArrayList<ItemField>();

			for(ItemField i : all_fields){
				if( !l.hasField(i.fieldName) ){
					other_fields.add(i);
				}
			};
			
			map.put("selected_fields", selected_fields);			
			map.put("other_fields", other_fields);

			maps.add(map);
		}
		
		//Prepare template and data for excel export
    	String filepath = Play.applicationPath + File.separator + 
    		"app" + File.separator + "views" + File.separator + "projects"
    		+ File.separator + "export.xls";
    	VirtualFile templateFile = VirtualFile.open(filepath);
    	InputStream inputStream = templateFile.inputstream();
    	
    	MiscUtil.ConsoleLog("use sync excel rendering");
        long start = System.currentTimeMillis();
    	try {
    		Workbook workbook = new XLSTransformer().transformMultipleSheetsList(
    				inputStream, maps, sheetNames, "map", new HashMap(), 0);
    		workbook.write(response.out);
    		inputStream.close();
			MiscUtil.ConsoleLog("Excel sync render takes " + (System.currentTimeMillis() - start));
		} catch (Exception e) {
			MiscUtil.ConsoleLog("ERROR: " + e.getMessage());
			error();
		} 
		
		//Set correct response header
    	try {
			response.setHeader("Content-Disposition",
			        "attachment; filename=" + (new URLCodec("utf-8").encode(p.name)) + ".xls");
		} catch (EncoderException e) {
			response.setHeader("Content-Disposition",
			        "attachment; filename=exported.xls");
		}
    	response.setContentTypeIfNotSet("application/vnd.ms-excel");
    	
	}
	
	public static void trash(Long project_id){
		renderArgs.put("active", "settings");
		Project p = getActiveProject();
		if(p == null){error(400, "Bad Request");}
		List<Item> deleted_items = Item.findDeleted(p);
		render(deleted_items);
	}
}
