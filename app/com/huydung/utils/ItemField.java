package com.huydung.utils;

public class ItemField {
	public String name;
	public String fieldName;
	public String description;
	public String inputHelp;
		
	public ItemField(String fieldName, String name) {
		super();
		this.name = name;
		this.fieldName = fieldName;
	}
	
	
	public ItemField(String fieldName, String name,String description) {
		this(fieldName, name);
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
}
