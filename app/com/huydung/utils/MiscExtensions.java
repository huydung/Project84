package com.huydung.utils;

import java.util.Date;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;

import play.i18n.Messages;
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
	
	public static String timeLeft(Date date) {
        Date now = new Date();
        if (now.after(date)) {
            return "";
        }
        long delta = (date.getTime() - now.getTime()) / 1000;
        if (delta < 60) {
            return Messages.get("left.seconds", delta);
        }
        if (delta < 60 * 60) {
            long minutes = delta / 60;
            return Messages.get("left.minutes", minutes);
        }
        if (delta < 24 * 60 * 60) {
            long hours = delta / (60 * 60);
            return Messages.get("left.hours", hours);
        }
        if (delta < 30 * 24 * 60 * 60) {
            long days = delta / (24 * 60 * 60);
            return Messages.get("left.days", days);
        }
        if (delta < 365 * 24 * 60 * 60) {
            long months = delta / (30 * 24 * 60 * 60);
            return Messages.get("left.months", months);
        }
        long years = delta / (365 * 24 * 60 * 60);
        return Messages.get("left.years", years);
    }
}
