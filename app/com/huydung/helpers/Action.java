package com.huydung.helpers;

public class Action {
	public String link;
	public String text;
	public String title;
	public String classes;
	
	public Action(String link, String text) {
		super();
		this.link = link;
		this.text = text;
		this.title = text;
	}

	public Action(String link, String text, String classes) {
		super();
		this.link = link;
		this.text = text;
		this.classes = classes;
		this.title = text;
	}
	
}
