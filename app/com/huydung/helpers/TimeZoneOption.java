package com.huydung.helpers;

public class TimeZoneOption {
	private String id;
	private String label;
	public TimeZoneOption(String id, String timezone) {
		super();
		this.id = id;
		this.label = timezone;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getLabel() {
		return label;
	}
	public void setLabel(String label) {
		this.label = label;
	}
	
}
