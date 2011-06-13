package models.filters;

import java.util.Map;

import com.huydung.utils.ItemField;

import play.mvc.Scope.Params;
import play.mvc.Scope.RenderArgs;

import models.Listing;

public abstract class BasicFilter {	
	protected String fieldName;
	protected Listing listing;
	
	public BasicFilter(ItemField field, Listing listing){
		this.fieldName = field.fieldName;
		this.listing = listing;
	}
	public abstract void setDefault(Params params);
	public abstract String getJPQL(Params params);
	public abstract String getIncludeFile();
	
}
