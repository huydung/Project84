package models.filters;

import java.util.Map;

import com.huydung.utils.ItemField;

import play.mvc.Scope.Params;

import models.Listing;
import models.Project;

public class FilterCheck extends BasicFilter {
	
	public FilterCheck(ItemField field, Listing listing) {
		super(field, listing);
	}
	
	@Override
	public String getJPQL(Params params) {
		String t = params.get("filter_checkbox_true");
		String f = params.get("filter_checkbox_false");
		boolean showChecked = t != null && t.equals("on");
		boolean showUnChecked = f != null && f.equals("on");
		if( showChecked && showUnChecked || (!showChecked && !showUnChecked) ){
			return "";			
		} else if( showChecked ){
			return " checkbox = 1";
		} else{
			return " checkbox = 0";
		}
	}
	
	@Override
	public String getIncludeFile() {
		return "items/filters/checkbox.html";
	}

	@Override
	public void setDefault(Params params) {
		if( !params._contains("filter_checkbox_true") ){
			params.put("filter_checkbox_true", "on");
		}
		if( !params._contains("filter_checkbox_false") ){
			params.put("filter_checkbox_false", "on");
		}
	}

}
