package controllers;

import java.util.List;

import models.Project;
import models.ProjectTemplate;
import models.User;
import play.data.validation.Valid;
import play.mvc.*;

public class Projects extends AppController {    

    public static void create(){
    	Project project = new Project();
    	List<ProjectTemplate> templates = ProjectTemplate.getTemplates(getLoggedin());
    	render(project, templates);
    }
    
    public static void save(){
    	Project project = new Project();    	
    	render(project);
    }

}
