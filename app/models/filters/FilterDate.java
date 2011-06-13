package models.filters;

import models.Listing;

import com.huydung.utils.ItemField;

import play.mvc.Scope.Params;

public class FilterDate extends BasicFilter {

	public FilterDate(ItemField field, Listing listing) {
		super(field, listing);
	}

	@Override
	public void setDefault(Params params) {
		// TODO Auto-generated method stub

	}

	@Override
	public String getJPQL(Params params) {
		// TODO Auto-generated method stub
		return "";
	}

	@Override
	public String getIncludeFile() {	
		return "items/filters/date.html";
	}

}
