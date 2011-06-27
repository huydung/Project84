package models.vos;

import play.templates.JavaExtensions;
import models.Project;

public class VProject {
	public String name;
	public String description;
	public String deadline;
	public String created;
	public String creator;
	
	public static VProject createFromProject(Project p){
		VProject vp = new VProject();
		vp.name = p.name;
		vp.description = p.description == null ? "" : p.description;
		vp.deadline = p.deadline == null ? "<not set>" : JavaExtensions.format(p.deadline);
		vp.created = JavaExtensions.format(p.created);
		vp.creator = p.creator.fullName + " (" + p.creator.nickName + ")";		
		return vp;
	}
}
