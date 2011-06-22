package models.validators;

import models.templates.Template;
import models.templates.ListTemplate;
import models.templates.ProjectTemplate;

import play.data.validation.Check;

public class MustHaveUserIfNotSystem extends Check {

	public boolean isSatisfied(Object template, Object user) {
	  if( !((Template)template).isSystem && user == null ){
		  return false;
	  }
	  return true;
	}	
}
