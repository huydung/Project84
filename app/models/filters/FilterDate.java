package models.filters;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.lang.time.DateUtils;

import models.Listing;

import com.huydung.utils.ItemField;

import play.Play;
import play.data.binding.types.DateBinder;
import play.libs.I18N;
import play.mvc.Scope.Params;
import play.templates.JavaExtensions;

public class FilterDate extends BasicFilter {

	public FilterDate(ItemField field, Listing listing) {
		super(field, listing);
	}

	@Override
	public void setDefault(Params params) {
		//set Default to only display items from the last 3 days to the next 14 days
		
		Date d = new Date();
		/*
		d.setTime(d.getTime() - 3 * DateUtils.MILLIS_PER_DAY);
		params.put("filter_date_from", JavaExtensions.format(d));
		*/
		d.setTime(d.getTime() + 17 * DateUtils.MILLIS_PER_DAY);
		params.put("filter_date_to", JavaExtensions.format(d));
		
	}

	@Override
	public String getJPQL(Params params) {
		SimpleDateFormat sdf = new SimpleDateFormat(Play.configuration.getProperty("date.format"));
        sdf.setLenient(false);
		Date dateStart = null;
		Date dateEnd = null;
		try {
			dateStart = sdf.parse(params.get("filter_date_from"));
			dateEnd = sdf.parse(params.get("filter_date_to"));
		} catch (Exception e) {
			// Nothing to do here, silently ignore error			
		}
			
		if( dateStart != null && dateEnd != null ){
			//Because we want to include the last Date in range, we add 1 day to dateEnd
			dateEnd.setTime(dateEnd.getTime() + DateUtils.MILLIS_PER_DAY);
			return " (date IS NULL OR (date >= '" + JavaExtensions.format(dateStart, "yyyy-MM-dd") + "'" +
				" AND date <= '"+ JavaExtensions.format(dateEnd, "yyyy-MM-dd")  +"'))";
		}else if( dateStart != null ){
			return " (date IS NULL OR date >= '" + JavaExtensions.format(dateStart, "yyyy-MM-dd") + "')" ;
		}else if( dateEnd != null){
			dateEnd.setTime(dateEnd.getTime() + DateUtils.MILLIS_PER_DAY);
			return " (date IS NULL OR date <= '"+ JavaExtensions.format(dateEnd, "yyyy-MM-dd")  +"')";	
		}
		return "";
	}

	@Override
	public String getIncludeFile() {	
		return "items/filters/date.html";
	}

}
