package com.huydung.utils;

import models.Item;
import play.i18n.Messages;

public class ItemField {
	public String name;
	public String fieldName;
	public String description;
	public String inputHelp;
	
	public ItemField(String fieldName){
		super();
		this.fieldName = fieldName;
		this.name = Messages.get("f."+fieldName);
		this.description = Messages.get("f."+fieldName+".des");
	}
	
	public ItemField(String fieldName, String name) {
		super();
		this.name = name;
		this.fieldName = fieldName;
		this.description = Messages.get("f."+fieldName+".des");
	}
	
	
	public ItemField(String fieldName, String name,String description) {
		this.name = name;
		this.fieldName = fieldName;
		this.description = description;
	}
	
	public ItemField(String fieldName, String name, String description, String inputHelp) {
		this(fieldName, name, description);
		this.inputHelp = inputHelp;
	}
	
	@Override
	public String toString(){
		return name;
	}
	
	public boolean isFilterable(){
		//MiscUtil.ConsoleLog("Check if " + fieldName +" is filterable");
		return Item.FIELDS_FILTERABLE.contains(fieldName);
	}
}
