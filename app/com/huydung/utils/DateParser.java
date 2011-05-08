package com.huydung.utils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DateParser {
	
	private Calendar base;
	private String timezoneId;
	private String resultFormat;
	private SimpleDateFormat formatter = new SimpleDateFormat();
	
	public String getResultFormat() {
		return resultFormat;
	}

	public void setResultFormat(String resultFormat) {
		this.resultFormat = resultFormat;
		formatter.applyPattern(resultFormat);
	}

	public Calendar getBase() {
		return base;
	}

	public void setBase(Calendar base) {
		this.base = base;
		if(timezoneId != null){
			base.setTimeZone(TimeZone.getTimeZone(timezoneId));
		}
	}
	
	public String getTimezoneId() {
		return timezoneId;
	}

	public void setTimezoneId(String timezoneId) {
		this.timezoneId = timezoneId;
		base.setTimeZone(TimeZone.getTimeZone(timezoneId));
		formatter.setTimeZone(TimeZone.getTimeZone(timezoneId));
	}

	public DateParser(){		
		setBase(new GregorianCalendar());
		setTimezoneId("GMT+00:00");
		setResultFormat("yyyy-dd-MM HH:mm Z");		
		
	}
	
	public DateParser(Calendar base, String timezoneId, String format){		
		setBase(base);	
		setTimezoneId(timezoneId);
		setResultFormat(format);
	}
	
	public Date parseDate(String input){
		Calendar temp = (Calendar) base.clone();
		
		//Parse relative Date		 
		if( input.contains("@tomorrow") ){
			temp.add(Calendar.DAY_OF_YEAR, 1);
			return temp.getTime();
		}else if( input.contains("@yesterday") ){
			temp.add(Calendar.DAY_OF_YEAR, -1 );
			return temp.getTime();
		}
		
		Pattern p = Pattern.compile("@[\\+-]? day(s)? ((ahead)|(ago))?", Pattern.CASE_INSENSITIVE);
		Matcher m = p.matcher(input);
		
		return temp.getTime();
	}
	
	public String parseDateWithFormat(String input){
		Date temp = parseDate(input);
		return formatter.format(temp);
	}
	
}
