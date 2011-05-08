package com.huydung.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Map;
import java.util.TimeZone;
import java.util.TreeMap;

import com.huydung.helpers.DateFormatOption;
import com.huydung.helpers.TimeZoneOption;

public class SelectorUtil {
	
	public static ArrayList<TimeZoneOption> getTimezones(){
		String[] ids = TimeZone.getAvailableIDs();
		final String TIMEZONE_ID_PREFIXES =
		      "^(Africa|America|Asia|Atlantic|Australia|Europe|Indian|Pacific)/.*";
		ArrayList<TimeZoneOption> timezones = new ArrayList<TimeZoneOption>();
		
		for( String id : ids ){
			if( id.matches(TIMEZONE_ID_PREFIXES) ){
				TimeZone tz = TimeZone.getTimeZone(id);

				int offset = tz.getRawOffset();
				int hour = Math.abs(offset) / (60*60*1000);
				String h = (hour > 9 ? "" : "0") + hour; 
			    int min = Math.abs(offset / (60*1000)) % 60;
			    String m = (min > 9 ? "" : "0") + min; 
			    String sign = offset > 0 ? "+" : "-";
			    String prefix = "(GMT";
			    if(hour != 0 || min != 0){
			    	prefix += sign + h + ":" + m;
			    }
			    prefix += ") ";
			    TimeZoneOption tzo = new TimeZoneOption(id, prefix + id);
			    timezones.add(tzo);
			}			
		}
		
		Collections.sort(timezones, new Comparator<TimeZoneOption>() {
			@Override
			public int compare(TimeZoneOption o1, TimeZoneOption o2) {
				int result = o1.getLabel().compareTo(o2.getLabel());
				if( result == 0 ){
					result = o1.getId().compareTo(o2.getId());
				}
				return result;
			}
		});
		return timezones;
	}
	
	public static ArrayList<DateFormatOption> getDateFormats(){
		ArrayList<DateFormatOption> formats = new ArrayList<DateFormatOption>();
		formats.add(new DateFormatOption("dd/MM/YYYY", "14/06/2011"));
		formats.add(new DateFormatOption("MM/dd/YYYY", "06/14/2011"));
		formats.add(new DateFormatOption("dd MMM, YYYY", "14 Jun, 2011"));
		formats.add(new DateFormatOption("MMMMM dd, YYYY", "June 14, 2011"));
		formats.add(new DateFormatOption("dd-MM-YYYY", "14-06-2011"));
		formats.add(new DateFormatOption("MM-dd-YYYY", "06-14-2011"));
		formats.add(new DateFormatOption("dd.MM.YYYY", "14.06.2011"));
		formats.add(new DateFormatOption("MM.dd.YYYY", "06.14.2011"));
		formats.add(new DateFormatOption("dd/MM", "14/06"));
		formats.add(new DateFormatOption("MM/dd", "06/14"));		
		formats.add(new DateFormatOption("dd-MM", "14-06"));
		formats.add(new DateFormatOption("MM-dd", "06-14"));		
		formats.add(new DateFormatOption("dd.MM", "14.06"));
		formats.add(new DateFormatOption("MM.dd", "06.14"));
		return formats;
	}
}
