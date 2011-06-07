package models;

import java.io.File;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Currency;
import java.util.Date;
import java.util.List;

import javax.persistence.Lob;

import com.huydung.utils.ItemField;

import models.enums.ItemType;

import play.data.validation.MaxSize;
import play.data.validation.Required;
import play.data.validation.URL;
import play.db.jpa.Blob;
import play.i18n.Messages;

public class Item extends BasicItem{
	
	public Item(ItemType type) {
		super();
		this.type = type.getName();
	}

	@MaxSize(500)
	public String description;
	
	@Lob
	public String body;
	
	/* Simple Attributes */
	public Date date; //Can either be Due Date or Date
	public Boolean date_hasTime;
	public Integer number;
	public User user;
	public String category;
	public Boolean check; 
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
	
	public static String getRequiredFields(){
		return  "created,creator,updated,type,name,id";
	}
	
	public static List<ItemField> getItemFields(){
		ArrayList<ItemField> iFields = new ArrayList<ItemField>();
		Class itemClass = Item.class;
		
		Field[] fields = itemClass.getFields();
		for( Field f : fields ){			
			String fName = f.getName();
			if(!"willBeSaved,type".contains(fName)){
				if( !fName.contains("_") ){
					String localeTitle = "f."+fName;
					String localeDes = "f."+fName+".des";
					String localeInputHelper = "f."+fName+".iph";
					iFields.add(new ItemField(fName,
							Messages.get(localeTitle),
							Messages.get(localeDes),
							Messages.get(localeInputHelper)
					));
				}
			}
		}
		
		return iFields;
	}
	
}
