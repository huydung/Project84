package models.filters;

import com.huydung.utils.ItemField;

import models.Listing;

public class FilterFactory {
	public static BasicFilter createFilter(ItemField field, Listing listing){
		if( field.isFilterable() ){
			if( field.fieldName.equals("checkbox") ){
				return new FilterCheck(field, listing);
			}else if( field.fieldName.equals("user") ){
				return new FilterUser(field, listing);
			}else if( field.fieldName.equals("date") ){
				return new FilterDate(field, listing);
			}
		}
		return null;
	}
}
