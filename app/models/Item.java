package models;

import java.io.File;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Currency;
import java.util.Date;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import com.huydung.utils.ItemField;
import com.huydung.utils.MiscUtil;

import models.enums.ItemType;

import play.data.validation.MaxSize;
import play.data.validation.Required;
import play.data.validation.URL;
import play.db.jpa.Blob;
import play.i18n.Messages;
import play.templates.JavaExtensions;

@Entity
public class Item extends BasicItem{
	
	public static final String FIELDS_FILTERABLE = "date,number,user,category,checkbox";
	public static final String FIELDS_REQUIRED = "listing,created,creator,updated,type,name,id";
	public static final String FIELDS_SUBINFO = "date,number,user,phone1,email1,cost,file";
	@MaxSize(500)
	public String description;
	
	@Lob
	public String body;
	
	/* Simple Attributes */
	public Date date; //Can either be Due Date or Date
	
	public Boolean date_hasTime;
	public Integer number;
	@ManyToOne(fetch=FetchType.EAGER)
	public User user;
	public String category;
	public Boolean checkbox; 
	@URL
	public String url;
	
	/* Contact Related Attributes */	
	public String phone1;
	public String phone2;
	public String email1;
	public String email2;
	
	/* Address Related Attributes */
	public String address;
	public Long address_lat;
	public Long address_lan;
	
	/* File Attributes */
	public Blob file;
	public String file_mimeType;
	public Long file_size;
	
	/* Asset Attributes */
	public BigDecimal cost;
	public Integer cost_amount;
	public String cost_currency;
	
	@ManyToOne
	public Listing listing;
		
	public Item(Listing listing) {
		super();
		this.listing = listing;
	}
	
	public static List<ItemField> getItemFields(){
		ArrayList<ItemField> iFields = new ArrayList<ItemField>();
		Class itemClass = Item.class;
		
		Field[] fields = itemClass.getFields();
		for( Field f : fields ){			
			String fName = f.getName();
			if(!"willBeSaved".contains(fName)){
				if( !fName.contains("_") ){
					iFields.add(new ItemField(fName));
				}
			}
		}
		
		return iFields;
	}
	
	public String getClasses(){
		String res =  "item";
		res += " item-" + JavaExtensions.slugify(listing.listingName);
		if( checkbox == true ){
			res += " checked";
		}
		if( date != null ){
			Date now = new Date();
			if( date.getTime() < now.getTime() ){
				res += " past";
			}else if( date.getTime() > now.getTime() ){
				res += " future";
			}
		}
		if( description != null ){
			res += " has_description";
		}
		return res;
	}
	
	public static List<Item> findByListing(Listing listing, String filter){
		String query = "listing = ?";
		if( filter != null && filter.length() > 0 ){
			query += " AND" + filter;
		}
		query +=  "ORDER BY " + listing.sort;
		MiscUtil.ConsoleLog(query);
		return Item.find(query, listing).fetch();
	}
}
