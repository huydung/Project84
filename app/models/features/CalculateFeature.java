package models.features;

import java.math.BigDecimal;
import java.text.NumberFormat;

import org.apache.commons.lang.StringEscapeUtils;
import play.Logger;
import play.db.jpa.JPA;
import play.i18n.Messages;

import com.huydung.utils.MiscExtensions;
import com.huydung.utils.MiscUtil;
import models.Item;
import models.Listing;

public class CalculateFeature extends BasicFeature {
	
	protected CalculateMethod method;
	protected String field;
	
	public CalculateFeature(CalculateMethod method, String field, Listing l) {
		super("items/features/calculate.html");
		this.method = method;		
		this.field = field;
		this.listing = l;
		this.name = Messages.get("listing.feature." + method, l.getFieldName(field));
		this.identifier = method + "_" + field;
	}

	@Override
	public void process() {
		if( filterString != null ){
			String filter = "FROM Item JOIN BasicItem ON Item.id = BasicItem.id WHERE deleted = 0 AND listing_id = "+ listing.id;
			if( !filterString.isEmpty() ){
				filter += " AND " + filterString;
			}
			String query = "";
			if( method == CalculateMethod.SUM ){
				query += "SELECT SUM(" + StringEscapeUtils.escapeSql(field) + ") " + filter;
			}else if( method == CalculateMethod.AVERAGE ){
				query += "SELECT AVG(" + StringEscapeUtils.escapeSql(field) + ") " + filter;
			}
			if( !query.isEmpty() ){
				MiscUtil.ConsoleLog(query);
				BigDecimal value = (BigDecimal)JPA.em().createNativeQuery(query).getSingleResult();
				result = MiscExtensions.format(value);
			}			
		}else{
			Logger.error("CalculateFeature should have filterString setted before use");
		}
	}

	@Override
	public String getResult() {
		if( result != null && !result.isEmpty() ){
			return Messages.get(
					"listing.feature."+method+".result", 
					listing.getFieldName(field), result);
		}
		return "";
	}

}
