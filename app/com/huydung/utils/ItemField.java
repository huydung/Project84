package com.huydung.utils;

public class ItemField {
	private String name;
	private String fieldName;
	private String description;
	private String inputHelp;
		
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

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getInputHelp() {
		return inputHelp;
	}
	public void setInputHelp(String inputHelp) {
		this.inputHelp = inputHelp;
	}

	public String getFieldName() {
		return fieldName;
	}

	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}
	
	
}
