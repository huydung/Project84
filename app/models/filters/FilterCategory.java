package models.filters;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.hibernate.mapping.Array;

import com.huydung.utils.ItemField;
import com.huydung.utils.MiscUtil;

import play.i18n.Messages;
import play.mvc.Scope.Params;
import play.templates.JavaExtensions;

import models.Item;
import models.Listing;
import models.User;

public class FilterCategory extends BasicFilter {

	private String selectedCategories;
	private List<String> categories;
	
	public FilterCategory(ItemField field, Listing listing) {
		super(field, listing);
		categories = listing.getCategories();
	}
	
	public List<String> getCategories(){		
		if( categories == null ){
			categories = listing.getCategories();
			if( categories == null ){
				categories = new ArrayList<String>();
			}
		}
		return categories;      
	}	
	
	@Override
	public String getJPQL(Params params) {
		if( categories != null ){
			boolean selectedAll = true;
			List<String> cats = new ArrayList<String>();
			for( String cate : categories ){
				if( cate != null ){
					String s = params.get("filter_category_" + JavaExtensions.slugify(cate));
					if( s != null && s.equals("on") ){
						cats.add("'" + cate + "'");			
					} else {
						selectedAll = false;
					}	
				}
			}
			if( selectedAll || cats.size() == 0){
				return "";
			}else{
				return " category IN ("+ StringUtils.join(cats, ",") +") ";
			}
		}else{
			MiscUtil.ConsoleLog("WTF, categories is null?");
		}
		return "";
	}


	@Override
	public String getIncludeFile() {
		return "items/filters/category.html";
	}

	@Override
	public void setDefault(Params params) {
		if( categories != null ){
			for( String cat : categories ){
				if( cat != null ){
					params.put("filter_category_" + JavaExtensions.slugify(cat), "on");
				}
			}
		}else{
			MiscUtil.ConsoleLog("WTF, categories is null?");
		}	
	}


}
