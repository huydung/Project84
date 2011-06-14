package com.huydung.utils;

import org.apache.commons.lang.StringUtils;

import play.templates.JavaExtensions;

public class MiscExtensions extends JavaExtensions {
	public static String abbr(String input, Integer length){
		if( length == null ){ length = 30; }
		return StringUtils.abbreviate(input, length);
	}
}
