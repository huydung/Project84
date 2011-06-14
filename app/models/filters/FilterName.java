package models.filters;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;

import models.Listing;

import com.huydung.utils.ItemField;

import play.mvc.Scope.Params;

public class FilterName extends BasicFilter{

	public FilterName(ItemField field, Listing listing) {
		super(field, listing);
	}

	@Override
	public void setDefault(Params params) {
		// Nothing need to do here		
	}

	@Override
	public String getJPQL(Params params) {
		String keyword = params.get("filter_name_keyword");
		if( keyword != null ){
			return " name LIKE '%"+ StringEscapeUtils.escapeSql(keyword) +"%'";
		}
		return null;
	}

	@Override
	public String getIncludeFile() {
		return "items/filters/name.html";
	}

}
