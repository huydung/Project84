package models;

import java.io.File;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Currency;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import com.huydung.utils.ItemField;
import com.huydung.utils.Link;
import com.huydung.utils.MiscUtil;

import models.enums.ItemType;

import play.data.validation.MaxSize;
import play.data.validation.Required;
import play.data.validation.URL;
import play.db.jpa.Blob;
import play.i18n.Messages;
import play.mvc.Router;
import play.templates.JavaExtensions;
import sun.misc.Regexp;
import sun.util.resources.CurrencyNames;

@Entity
public class Item extends BasicItem implements IWidgetItem{
	
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
		query +=  " ORDER BY " + listing.sort;
		MiscUtil.ConsoleLog(query);
		return Item.find(query, listing).fetch();
	}

	@Override
	public String getSubInfo() {
		return getValueOfField(this.listing.subField);
	}

	@Override
	public Link getInfo() {
		Map<String, Object> args = new HashMap<String, Object>();
		args.put("project_id", this.listing.project.id);
		args.put("id", this.id);
		return new Link(
			getValueOfField(this.listing.mainField), 
			Router.getFullUrl("Items.show", args),
			"modal"
		);
	}
	
	public String getValueOfField(String field){
		
		if( field.equals("date") ){
			if( this.date != null ){
				return JavaExtensions.format(this.date);
			}
		}
		if( field.equals("created") ){
			if( this.created != null ){
				return JavaExtensions.since(this.created);
			}
		}
		if( field.equals("user") ){
			if( this.user != null ){
				Map<String, Object> args = new HashMap<String, Object>();
				args.put("project_id", this.listing.project.id);
				args.put("id", this.user.id);
				String url = Router.getFullUrl("Memberships.show", args);
				return "<a href=\""+url+"\" class=\"modal\">" + this.user.fullName + "</a>";
			}else{
				return "";
			}
		}
		
		Class itemClass = Item.class;
		
		Field[] fields = itemClass.getFields();
		for( Field f : fields ){			
			if( f.getName().equals(field) ){
				try {
					Object value = f.get(this);
					return value != null ? (String)value : "";
				} catch (Exception e) {
					MiscUtil.ConsoleLog("Error occured in Item.getValueOfField()");
				}
			}
		}
		return "";
	}
	
	public static Item createFromSmartInput(String input, Listing l){
		//We'll add a blank character before and after the input string
		//so our parser can guarantee that keyword are prefix/suffix by a \s
		String name = " " + input + " ";		
		Item item = new Item(l);		
		
		//TODO: Extract Date
		name = parseNumber(name, item);
		name = parseCost(name, item);
		name = parseEmail(name, item);
		name = parseTelephone(name, item);
		name = parseAddress(name, item);
		//TODO: Extract Address
		
		
		//Clean up the name after processing
		name = Pattern.compile("\\s{2,}").matcher(name).replaceAll(" ");
		name = name.trim();
		item.name = name;
		return item;
	}
	
	private static String parseAddress(String name, Item item){
		Matcher addMatcher = Pattern.compile(
				"[\\s]add:(.+)#").matcher(name);
		while( addMatcher.find() ){	
			String address = addMatcher.group().trim().substring(4);
			item.address = address.substring(0, address.length() - 1);			
		}
		if( item.address != null ){
			name = addMatcher.replaceAll(" ");
		}
		return name;
	}

	private static String parseTelephone(String name, Item item){
		Matcher phoneMatcher = Pattern.compile(
			"[\\s]tel:([\\d]{6,15})([,]([\\d]{6,15}))*").matcher(name);
		
		while( phoneMatcher.find() ){	
			String phones = phoneMatcher.group().trim().substring(4);			
			if( !phones.contains(",") ){
				if(item.phone2 != null){
					item.phone1 = item.phone2;
				}
				item.phone2 = phones;
			}else{
				String[] parts = phones.split(",");
				item.phone1 = parts[ parts.length - 2 ];
				item.phone2 = parts[ parts.length - 1 ];
			}			
		}
		if( item.phone1 == null ){
			item.phone1 = item.phone2; item.phone2 = null;
		}
		name = phoneMatcher.replaceAll(" ");
		return name;
	}
	
	private static String parseCost(String name, Item item) {
		Matcher costMatcher = Pattern.compile(
			"[\\s]([a-zA-Z]{3})[ ]?([\\d]+[\\.]?[\\d]+([xX][\\d]+)?)"
		).matcher(name);
		String costFound = "";
		while( costMatcher.find() ){
			costFound = costMatcher.group().trim();
		};
		if( costFound.length() > 0 ){
			String currencyCode = costFound.substring(0, 3).toUpperCase();
			try{
				Currency c = Currency.getInstance(currencyCode);
			
				if( c!= null && c.getDefaultFractionDigits() > -1 ){
					String priceAndAmount = costFound.substring(3).trim().toLowerCase();
					item.cost_currency = currencyCode;
					if( priceAndAmount.contains("x") ){
						String[] parts = priceAndAmount.split("x");					
						item.cost = new BigDecimal(parts[0]);
						item.cost_amount = new Integer(parts[1]);
					}else{
						item.cost_amount = 1;
						item.cost = new BigDecimal(priceAndAmount);
					}
					name = costMatcher.replaceAll(" ");
				}
			}catch(Exception e){
				//We do not need to do anything here.
			}

		}
		return name;
	}

	private static String parseNumber(String name, Item item) {
		Matcher numMatcher = Pattern.compile("[\\s]num:([\\d]+)").matcher(name);
		String numFound = "";
		while( numMatcher.find() ){	numFound = numMatcher.group().trim(); }
		if( numFound.length() > 0 ){
			String[] parts = numFound.split(":");
			item.number = Integer.parseInt(parts[1]);
		}
		name = numMatcher.replaceAll(" ");		
		return name;
	}

	private static String parseEmail(String name, Item item) {
		Matcher emailMatcher = Pattern.compile("[\\s][a-z_\\.0-9]*[a-z0-9]+@[a-z0-9\\-]+([\\.][a-z]+){1,2}").matcher(name);
		
		while( emailMatcher.find() ){	
			if(item.email2 != null){
				item.email1 = item.email2;
			}
			item.email2 = emailMatcher.group().trim();
		}
		if( item.email1 == null ){
			item.email1 = item.email2; item.email2 = null;
		}
		name = emailMatcher.replaceAll(" ");
		return name;
	}
}
