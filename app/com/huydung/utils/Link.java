package com.huydung.utils;

public class Link {
	public String title;
	public String href = "#";
	public String target = "_self";
	public String className = "";
	
	public Link(String title, String href) {
		super();
		this.title = title;
		this.href = href;
	}
	
	public Link(String title, String href, String className) {
		this(title, href);
		this.className = className;
	}

	public Link(String title, String href, String className, String target) {
		this(title, href, className);
		this.target = target;
	}	
	
}
