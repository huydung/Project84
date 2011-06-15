package com.huydung.utils;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;

import play.templates.JavaExtensions;

public class MiscExtensions extends JavaExtensions {
	public static String abbr(String input, Integer length){
		if( length == null ){ length = 30; }
		return StringUtils.abbreviate(input, length);
	}
	
	public static String textOnly(String input){
		return input.replaceAll("\\<.*?\\>", "");
	}
	
	public static String html(String input){		
		//input = StringEscapeUtils.escapeJavaScript(input);
		input.replaceAll("\\<((script)|(iframe)).*?\\<\\/((script)|(iframe))\\>", "");
		//StringEscapeUtils.
		//remove header tags
		input = input.replaceAll("\\<h[1-6].*?\\>", "<br/><br/><strong>");
		input = input.replaceAll("\\<\\/h[1-6]\\>", "</strong><br/>");		
		//remove inline style
		input = input.replaceAll("style=\".*\"", "");
		return input;
	}
}
