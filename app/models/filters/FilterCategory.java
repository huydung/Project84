package models.filters;

import java.util.List;
import java.util.Map;

import com.huydung.utils.ItemField;

import play.mvc.Scope.Params;

import models.Item;
import models.Listing;

public class FilterCategory extends BasicFilter {

	private String selectedCategories;
	
	public FilterCategory(ItemField field, Listing listing) {
		super(field, listing);
	}
	
	public List<String> getCategories(){
		List<String> categories = Item.find("SELECT DISTINCT ? FROM Item i WHERE listing = ?", fieldName).fetch();
		return categories;      
	}	
	
	@Override
	public String getJPQL(Params params) {
		//if( params.containsKey("filter_category) )
		return "";
	}


	@Override
	public String getIncludeFile() {
		return "items/filters/category.html";
	}

	@Override
	public void setDefault(Params params) {
		// TODO Auto-generated method stub
		
	}


}
