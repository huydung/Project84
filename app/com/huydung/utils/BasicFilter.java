package com.huydung.utils;

import java.util.Map;

import play.mvc.Scope.Params;

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
