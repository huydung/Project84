package com.huydung.utils;

import models.Listing;

public class FilterFactory {
	public static BasicFilter createFilter(ItemField field, Listing listing){
		if( field.isFilterable() ){
			if( field.fieldName.equals("checkbox") ){
				return new FilterCheck(field, listing);
			}
		}
		return null;
	}
}
