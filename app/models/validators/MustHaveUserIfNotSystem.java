package models.validators;

import models.templates.BaseTemplate;
import models.templates.ListTemplate;
import models.templates.ProjectTemplate;

import play.data.validation.Check;

public class MustHaveUserIfNotSystem extends Check {

	public boolean isSatisfied(Object template, Object user) {
	  if( !((BaseTemplate)template).isSystem && user == null ){
		  return false;
	  }
	  return true;
	}	
}
