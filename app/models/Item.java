package models;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Currency;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.PostLoad;
import javax.persistence.PrePersist;
import javax.persistence.Transient;

import models.enums.ActivityAction;

import org.apache.commons.lang.time.DateUtils;
import org.hibernate.Session;
import org.hibernate.annotations.Filter;

import play.Logger;
import play.data.validation.MaxSize;
import play.data.validation.URL;
import play.db.jpa.Blob;
import play.db.jpa.JPA;
import play.mvc.Router;
import play.templates.JavaExtensions;
import sun.security.action.GetLongAction;

import com.huydung.utils.ItemField;
import com.huydung.utils.Link;
import com.huydung.utils.MiscExtensions;
import com.huydung.utils.MiscUtil;

import controllers.AppController;

@Entity
@Filter(name="deleted")
public class Item extends BasicItem implements IWidgetItem, IActivityLoggabe{
	public static final String FIELDS_ORDERABLE = ",category,checkbox,date,user,";	
	public static final String FIELDS_FILTERABLE = ",date,number,user,category,checkbox,name,";
	public static final String FIELDS_REQUIRED = ",project,deleted,listing,created,creator,updated,type,id,rawInput,";
	public static final String SUPPORTED_FILE_ICONS = ",ac3,ace,ade,adp,ai,aif,au,avi,bat,bin,bmp,bup,cab,cat,chm,css,cue,dat,dcr,der,dic,divx,diz,dll,doc,docx,dos,dvd,dwg,emf,exc,fon,gif,hlp,html,ifo,inf,ini,ins,ip,iso,isp,java,jfif,jpeg,jpg,log,m4a,mid,mmf,mmm,mov,movie,mp2,mp2v,mp3,mp4,mpe,mpeg,mpg,mpv2,nfo,pdd,pdf,php,ppt,pptx,psd,rar,reg,rtf,scp,theme,tif,tiff,tlb,ttf,txt,uis,url,vbs,vcr,vob,wav,wba,wma,wmv,wpl,wri,wtx,xls,xlsx,xml,xsl,zap,zip,";
	public static final String IMAGES_EXTS = ",png,jpg,bmp,gif,jpeg,";
	private static final long serialVersionUID = 1L;
	private static List<ItemField> _itemFields = null;
	
	@Transient
	public Map<String, String> fields_data = null;
	
	@ManyToOne
	public Project project;
	
	@MaxSize(500)
	public String description;
	
	public String rawInput;
	
	@Lob
	public String body;
	
	/* Simple Attributes */
	public Date date; //Can either be Due Date or Date
	
	public Boolean date_hasTime;
	public Integer number;
	@ManyToOne(fetch=FetchType.EAGER)
	public User user;
	public String category;
	public Boolean checkbox = false; 
	@URL
	public String url;
	
	/* Contact Related Attributes */	
	public String phone1;
	public String phone2;
	public String email1;
	public String email2;
	
	/* Address Related Attributes */
	public String address;
	public String address_lat;
	public String address_lan;
	
	/* File Attributes */
	public Blob file;
	public String file_name;
	public String file_ext;
	public Long file_size;
	
	/* Asset Attributes */
	public BigDecimal cost;
	public Integer cost_amount;
	public String cost_currency;
	public BigDecimal cost_total;
	
	@ManyToOne
	public Listing listing;
			
	public Item(Listing listing) {
		super();
		this.listing = listing;
	}
	
	public Item(){
		super();
	}
	@PostLoad
	public void prepareData(){
		//Generate Map of Field and Value
		fields_data = new HashMap<String, String>();
		Class itemClass = Item.class;
		
		Field[] fields = itemClass.getFields();
		for( Field f : fields ){
			try {
				Object value = f.get(this);
				String res = value != null ? value.toString() : "";				
				fields_data.put(f.getName(), res);				
			} catch (Exception e) {
				MiscUtil.ConsoleLog("Error occured in Item.getValueOfField()");
			}
		};
		String c = this.cost != null ? 
				(this.cost_amount == 1 ? 
					JavaExtensions.formatCurrency(this.cost, this.cost_currency)
					: JavaExtensions.formatCurrency(this.cost_total, this.cost_currency))
				: "";		

		fields_data.put("cost", c);
		fields_data.put("file", this.file_name != null ? this.file_name : "");

	}	
	
	@PrePersist
	public void beforeSave(){
		super.beforeSave();
		if(this.rawInput == null){
			this.createSmartInput();
		}
		if( this.cost != null ){
			if(this.cost_amount == null){ this.cost_amount = 1; }
			if(this.cost_currency == null){ this.cost_currency = "USD"; }
			this.cost_total = this.cost.multiply(new BigDecimal(this.cost_amount));
		}else{
			this.cost_amount = null;
			this.cost_currency = null;
			this.cost_total = null;
		}
		if( this.address == null ){
			this.address_lan = null; this.address_lat = null;
		}else{
			//Fetch Google LatLan
		}		
		if( this.listing != null ){
			this.project = this.listing.project;
		}
		if( this.file != null && this.file.exists() ){
			
		}else{
			this.file = null;
			this.file_name = null;
			this.file_size = null;
			this.file_ext = null;
		}
	}
	
	public static List<ItemField> getItemFields(){
		if(_itemFields == null){
		_itemFields = new ArrayList<ItemField>();
		Class itemClass = Item.class;		
			Field[] fields = itemClass.getFields();
			for( Field f : fields ){			
				String fName = f.getName();
				if(!"willBeSaved".contains(fName)){
					if( !fName.contains("_") ){
						_itemFields.add(new ItemField(fName));
					}
				}
			}
		}
		
		return _itemFields;
	}
	
	public String getClasses(){
		String res =  "item";
		res += " item-" + JavaExtensions.slugify(listing.listingName);
		if( checkbox != null && checkbox == true ){
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
		return findByListing(listing, filter, listing.sort);
	}
	
	public static List<Item> findByListing(Listing listing, String filter, String sort){
		return findByListing(listing, filter, sort, 200);
	}
	
	public static List<Item> findSinceDate(Date d, Project p){
		return Item.find("project = ? AND created >= ?", p, d).fetch();
	}
	
	public static List<Item> findByListing(Listing listing, String filter, String sort, int limit){
		String query = "deleted = 0 AND listing = ?";
		if( filter != null && !filter.isEmpty() ){
			query += " AND" + filter;
		}
		if( sort != null && !sort.isEmpty() ){
			query +=  " ORDER BY " + sort;
		}
		query += " LIMIT " + limit;
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
		args.put("item_id", this.id);
		return new Link(
			getValueOfField(this.listing.mainField), 
			Router.getFullUrl("Items.show", args),
			"modal"
		);
	}
	
	public boolean hasData(String field){
		if( Item.FIELDS_REQUIRED.contains("," + field + ",") ){
			return false;
		}
		if(field.equals("rawInput")){
			return false;
		}
		if(field.equals("file")){
			return this.file_name != null;
		}
		Field[] fields = Item.class.getFields();
		for( Field f : fields ){			
			if( f.getName().equals(field) ){
				try {
					Object value = f.get(this);
					return value != null && !value.toString().isEmpty();
				} catch (Exception e) {
					return false;
				}
			}
		}
		return false;
	}
	
	public String getValueOfField(String field){
		return getValueOfField(field, false, false);
	}
	
	public String getValueOfField(String field, Boolean raw, Boolean excelEscape){
		if( !raw ){
			if( field.equals("date") ){
				if( this.date != null ){return JavaExtensions.format(this.date);}
			}else if( field.equals("created") ){
				if( this.created != null ){	return JavaExtensions.since(this.created);	}
			}else if( field.equals("cost") ){
				if( this.cost != null ){
					return JavaExtensions.formatCurrency(this.cost_total, this.cost_currency);
				}
			}else if( field.equals("user") ){
				if( this.user != null ){
					Map<String, Object> args = new HashMap<String, Object>();
					args.put("project_id", this.listing.project.id);
					args.put("id", this.user.id);
					String url = Router.getFullUrl("Memberships.show", args);
					return "<a href=\""+url+"\" class=\"modal\">" + this.user.fullName + "</a>";
				}else{
					return "";
				}
			} else if( field.equals("address") ){
				if( this.address != null ){
					return MiscExtensions.abbr(this.address, 30);
				}
			}else if( field.equals("description") ){
				if( this.description != null ){
					return MiscExtensions.abbr(this.description, 30);
				}
			}else if( field.equals("body") ){
				if( this.body != null ){
					return MiscExtensions.abbr(MiscExtensions.textOnly(this.body), 30);
				}
			}else if ( field.equals("checkbox") ){
				if( this.checkbox != null ){
					return this.listing.getFieldName("checkbox") + 
						" <input type=\"checkbox\" " + 
						(this.checkbox ? "checked=\"checked\"" : "") 
						+ "/>";
				}
			}
		}		
		String res = fields_data.get(field);
		if(excelEscape){
			res = res.replace("http://", "");							
		}
		return res;
	}
	
	@SuppressWarnings("deprecation")
	public void createSmartInput(){
		this.rawInput = this.name;
		if( this.hasData("cost") ){
			this.rawInput += " " + this.cost_currency.toUpperCase() + ":" + this.cost + "x" + this.cost_amount;			
		}
		if( this.hasData("date") ){
			Calendar cal = new GregorianCalendar();
			cal.setTimeZone(TimeZone.getDefault());
			cal.setTime(this.date);
			this.rawInput += " @" + cal.get(Calendar.DATE) + "/"  + (cal.get(Calendar.MONTH) + 1);
			int year = cal.get(Calendar.YEAR);
			if( year != new Date().getYear() ){
				this.rawInput += "/" + year;
			}
		}
		if( this.hasData("email1") ){
			this.rawInput += " " + this.email1;
		}
		if( this.hasData("email2") ){
			this.rawInput += " " + this.email2;
		}
		if( this.hasData("phone1") ){
			this.rawInput += " " + this.phone1;
		}
		if( this.hasData("phone2") ){
			this.rawInput += " " + this.phone2;
		}
		if( this.hasData("number") ){
			this.rawInput += " num:" + this.number;
		}
		if( this.hasData("user") ){
			this.rawInput += " !" + this.user.nickName;
		}
		if( this.hasData("address") ){
			this.rawInput += " add:" + this.address + "#";
		}
		if( this.hasData("category") ){
			this.rawInput += " <" + category + ">";
		}
		//this.save();
	}
	
	public void updateFromSmartInput(String input, User user){
		Item item2 = Item.createFromSmartInput(input, this.listing, user);
		//Loop non-null field in item2 and copy to this item		
		copyProperties(item2);		
	}
	
	public void copyProperties(Item item2){
		Field[] fields = Item.class.getFields();
		for( Field f : fields ){			
			try {
				String name = f.getName();
				if( name != "id" ){
					int modifier = f.getModifiers();
					if(Modifier.isPublic(modifier) && !Modifier.isStatic(modifier)){
						Object value = f.get(item2);
						if( value != null ){
							try {
								f.set(this, value);
							} catch (Exception e) {
								Logger.error("Error when setting Item field [%s] with value [%s]", f.getName(), value.toString());
							}
						}
					}
				}
			} catch (Exception e) {
				MiscUtil.ConsoleLog("Error occured in Item.getValueOfField()");
			}			
		}
	}

	public static Item createFromSmartInput(String input, Listing l, User user){
		Calendar now = new GregorianCalendar();
		return createFromSmartInput(input, l, now, user);
	}
	
	public static Item createFromSmartInput(String input, Listing l, Calendar basedDate, User user){
		//We'll add a blank character before and after the input string
		//so our parser can guarantee that keyword are prefix/suffix by a \s
		String name = " " + input + " ";		
		Item item = new Item(l);
		item.rawInput = input;
		if( l!= null ){
			if( l.project != null ){
			name = parseUser(name, item);
			}
		}
		name = parseDate(name, item, basedDate);
		name = parseNumber(name, item);		
		name = parseEmail(name, item);
		name = parseTelephone(name, item);
		name = parseAddress(name, item);
		name = parseCategory(name, item);
		name = parseCost(name, item);
		
		//Clean up the name after processing
		name = Pattern.compile("\\s{2,}").matcher(name).replaceAll(" ");
		name = name.trim();
		item.name = name;
		item.creator = user;
		return item;
	}
	
	private static String parseAddress(String name, Item item){
		Matcher addMatcher = Pattern.compile(
				"[\\s]add:(.+)#", Pattern.CASE_INSENSITIVE).matcher(name);
		while( addMatcher.find() ){	
			String address = addMatcher.group().trim().substring(4);
			item.address = address.substring(0, address.length() - 1);			
		}
		if( item.address != null ){
			name = addMatcher.replaceAll(" ");
		}
		return name;
	}
	
	private static String parseUser(String name, Item item){
		Matcher userMatcher = Pattern.compile(
				"[\\s]!([a-zA-Z1-9\\-._]+)", Pattern.CASE_INSENSITIVE).matcher(name);

			List<User> users = Membership.findUserByProject(item.listing.project);
			while( userMatcher.find() ){	
				String username = userMatcher.group(1).trim().toLowerCase();				
				for( User u : users ){
					if( u.nickName.toLowerCase().equals(username) ){
						item.user = u;
						break;
					}
				}
			}

		if( item.user != null ){
			name = userMatcher.replaceAll(" ");
		}
		return name;
	}

	private static String parseDate(String name, Item item, Calendar basedDate){
		//MiscUtil.ConsoleLog("Based Date: " + basedDate.toString());
		Calendar date = (Calendar)basedDate.clone();
		if( name.contains("@today ")){
			item.date = date.getTime();
			name = name.replaceAll("@today", " ");
		}
		else if( name.contains("@tomorrow ")){
			date.add(Calendar.DAY_OF_YEAR, 1);
			item.date = date.getTime();
			name = name.replaceAll("@tomorrow", " ");
		}
		else if( name.contains("@yesterday ") ){
			date.add(Calendar.DAY_OF_YEAR, -1);
			item.date = date.getTime();
			name = name.replaceAll("@yesterday", " ");
		}
		else if( name.contains("@next week ")){
			date.add(Calendar.WEEK_OF_YEAR, 1);
			date.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);			
			item.date = date.getTime();
			name = name.replaceAll("@next week", " ");
		}
		else if( name.contains("@last week ") ){
			date.add(Calendar.WEEK_OF_YEAR, -1);
			date.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
			item.date = date.getTime();
			name = name.replaceAll("@last week", " ");
		}
		else {
			boolean founded = false;
			//Format: next 3 days, -4days 
			Matcher nextPrevMatcher = Pattern.compile(
				"@((next )|(last ))?([+-])?[ ]?([\\d]+)[ ]?day[s]?", Pattern.CASE_INSENSITIVE).matcher(name);
			/** group index: 
			 * 1 -> next |last 
			 * 2 -> next |null
			 * 3 -> last |null
			 * 4 -> + | -
			 * 5 -> \d 
			 */
			while( nextPrevMatcher.find() ){
				String nextLast = nextPrevMatcher.group(1);
				int signFactor = nextLast == null ? 1 : (nextLast.trim().equals("next") ? 1 : -1);
				if( nextPrevMatcher.group(4) != null ){
					signFactor = nextPrevMatcher.group(4).equals("+") ? 1 : -1;
				}
				int number = Integer.parseInt(nextPrevMatcher.group(5), 10);
				date.add(Calendar.DAY_OF_YEAR, signFactor * number);
				item.date = date.getTime();
				founded = true;
			}
			if(founded){
				name = nextPrevMatcher.replaceAll(" ");
			}
			
			//Find date that specified using weekdays
			if( !founded ){
				HashMap<String, Integer> weekDays = new HashMap<String, Integer>();
				weekDays.put("mon", Calendar.MONDAY);weekDays.put("tue", Calendar.TUESDAY);
				weekDays.put("wed", Calendar.WEDNESDAY);weekDays.put("thu", Calendar.THURSDAY);
				weekDays.put("fri", Calendar.FRIDAY);weekDays.put("sat", Calendar.SATURDAY);
				weekDays.put("sun", Calendar.SUNDAY);
				//Format: next monday
				Matcher weekDayMatcher = Pattern.compile(
				"@((next )|(last ))?((mon(?:day)?)|(tue(?:sday)?)|(wed(?:nesday)?)|(thu(?:rsday)?)|(fri(?:day)?)|(sat(?:urday)?)|(sun(?:day)?))", Pattern.CASE_INSENSITIVE).matcher(name);
				/** group index: 
				 * 1 -> next |last 
				 * 2 -> next |null
				 * 3 -> last |null
				 * 4 -> monday|mon|tuesday|tue...
				 * 5 -> mon | null
				 * 6 -> tue | null
				 */
				while( weekDayMatcher.find() ){
					String direction = weekDayMatcher.group(1);
					if( direction == null ){ direction = "next"; }					
					direction = direction.trim();
					boolean goNext = direction.equals("next") ? true : false;
					
					String weekday = weekDayMatcher.group(4).trim().substring(0, 3).toLowerCase();
					date.set(Calendar.DAY_OF_WEEK, weekDays.get(weekday));
					date.getTime(); //Trigger the above setting
					if( DateUtils.isSameDay(date, basedDate) ){
						if( goNext ){							
							date.add(Calendar.DAY_OF_YEAR, 7); 
						}
						else{ date.add(Calendar.DAY_OF_YEAR, -7); }
					}
					while(( date.before(basedDate) && goNext )){
						date.add(Calendar.DAY_OF_YEAR, 7);
					}
					while( date.after(basedDate) && !goNext ){
						date.add(Calendar.DAY_OF_YEAR, -7);
					}
					item.date = date.getTime();
					
					founded = true;
				}
				if(founded){
					name = weekDayMatcher.replaceAll(" ");
				}
			}
			
			//Find date that specify using numbers
			if(!founded){
				//Format 14.06.2011
				Matcher dateMatcher = Pattern.compile(
					"@([\\d]{1,2})[-/.]([\\d]{1,2})([-/.]([\\d]{4}))?"
				).matcher(name);
				while( dateMatcher.find() ){
					String d = dateMatcher.group(1);
					String m = dateMatcher.group(2);
					String y = dateMatcher.group(4);
					if( y == null ){ y = "" + date.get(Calendar.YEAR); }
					date.set(Integer.parseInt(y, 10), Integer.parseInt(m, 10) - 1, Integer.parseInt(d, 10));
					item.date = date.getTime();
					founded = true;
				}
				if(founded){
					name = dateMatcher.replaceAll(" ");
				}
			}
			if(!founded){
				//Format: 2011-14-06
				Matcher dateMatcher = Pattern.compile(
					"@([\\d]{4})[-/.]([\\d]{1,2})[-/.]([\\d]{1,2})"
				).matcher(name);
				while( dateMatcher.find() ){
					String d = dateMatcher.group(3);
					String m = dateMatcher.group(2);
					String y = dateMatcher.group(1);
					if( y == null ){ y = "" + date.get(Calendar.YEAR); }
					date.set(Integer.parseInt(y, 10), Integer.parseInt(m, 10) - 1, Integer.parseInt(d, 10));
					item.date = date.getTime();
					founded = true;
				}
				if(founded){
					name = dateMatcher.replaceAll(" ");
				}
			}
			if(!founded){
				HashMap<String, Integer> months = new HashMap<String, Integer>();
				months.put("jan", Calendar.JANUARY);months.put("feb", Calendar.FEBRUARY);
				months.put("mar", Calendar.MARCH);months.put("apr", Calendar.APRIL);
				months.put("may", Calendar.MAY);months.put("jun", Calendar.JUNE);
				months.put("jul", Calendar.JULY);months.put("aug", Calendar.AUGUST);
				months.put("sep", Calendar.SEPTEMBER);months.put("oct", Calendar.OCTOBER);
				months.put("nov", Calendar.NOVEMBER);months.put("dec", Calendar.DECEMBER);
				// Format: 14 June 2011
				Matcher dateMatcher = Pattern.compile(
					"@([\\d]{1,2})[ ]?([a-z]{3,10})[ ]?([\\d]{4})?", Pattern.CASE_INSENSITIVE
				).matcher(name);
				while( dateMatcher.find() ){
					String m = dateMatcher.group(2).substring(0, 3).toLowerCase();
					if( months.containsKey(m) ){
						String d = dateMatcher.group(1);
						String y = dateMatcher.group(3);
						y = y == null ? "" + date.get(Calendar.YEAR) : y;
						date.set(Calendar.YEAR, Integer.parseInt(y, 10));
						date.set(Calendar.MONTH, months.get(m));
						date.set(Calendar.DAY_OF_MONTH, Integer.parseInt(d, 10));
						item.date = date.getTime();
						founded = true;
					}
				}
				if(founded){
					name = dateMatcher.replaceAll(" ");
				}else{
					dateMatcher = Pattern.compile(
						"@([a-z]{3,10})[ ]?([\\d]{1,2})[ ]?([\\d]{4})?", Pattern.CASE_INSENSITIVE
					).matcher(name);
					while( dateMatcher.find() ){
						String m = dateMatcher.group(1).substring(0, 3).toLowerCase();
						if( months.containsKey(m) ){
							String d = dateMatcher.group(2);
							String y = dateMatcher.group(3);
							y = y == null ? "" + date.get(Calendar.YEAR) : y;
							date.set(Calendar.YEAR, Integer.parseInt(y, 10));
							date.set(Calendar.MONTH, months.get(m));
							date.set(Calendar.DAY_OF_MONTH, Integer.parseInt(d, 10));
							item.date = date.getTime();
							founded = true;
						}
					}
					if(founded){
						name = dateMatcher.replaceAll(" ");
					}
				}
			}
		}
		
		return name;
	}
	
	private static String parseTelephone(String name, Item item){
		Matcher phoneMatcher = Pattern.compile(
			"[\\s]tel:([\\d]{6,15})([,]([\\d]{6,15}))*", Pattern.CASE_INSENSITIVE).matcher(name);
		
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
	
	private static String parseCategory(String name, Item item){
		Matcher categoryMatcher = Pattern.compile(
			" <(.+)>", Pattern.CASE_INSENSITIVE).matcher(name);
		
		while( categoryMatcher.find() ){	
			String category = categoryMatcher.group(1);			
			item.category = category;			
		}
		name = categoryMatcher.replaceAll(" ");
		return name;
	}
	
	private static String parseCost(String name, Item item) {
		Matcher costMatcher = Pattern.compile(
			"[\\s]([a-zA-Z]{3})?[ ]?([\\d]+[\\.]?[\\d]+([xX][\\d]+)?)", Pattern.CASE_INSENSITIVE
		).matcher(name);
		String costFound = "";
		String currencyCode = "";
		while( costMatcher.find() ){
			costFound = costMatcher.group(2).trim();
			currencyCode = costMatcher.group(1);			
			if( currencyCode == null ){
				if(costFound.contains("x")){
					currencyCode = "VND";
				}else{
					costFound = "";
					currencyCode = "";
				}
			}
			currencyCode = currencyCode.toUpperCase();
		};
		if( costFound.length() > 0 ){			
			try{
				Currency c = Currency.getInstance(currencyCode);
			
				if( c!= null && c.getDefaultFractionDigits() > -1 ){
					String priceAndAmount = costFound.toLowerCase();
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
		Matcher numMatcher = Pattern.compile(
				"[\\s]num:([\\d]+)", Pattern.CASE_INSENSITIVE).matcher(name);
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
		Matcher emailMatcher = Pattern.compile(
			"[\\s][a-z_\\.0-9]*[a-z0-9]+@[a-z0-9\\-]+([\\.][a-z]+){1,2}", Pattern.CASE_INSENSITIVE).matcher(name);
		
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
	
	public String getCostDisplay(){
		if( cost_amount > 1 ){
			return JavaExtensions.formatCurrency(cost_total, cost_currency) + " (" + JavaExtensions.formatCurrency(cost, cost_currency) + " x " + cost_amount + ")";
		}else{
			return JavaExtensions.formatCurrency(cost, cost_currency);
		}

	}
	
	public List<Comment> getComments(){
		return Comment.getCommentsOfItem(this);
	}
	
	public boolean create(User user){
		this.creator = user;
		boolean res = super.create();
		if( res ){
			Activity.track(this, user, ActivityAction.CREATE);
		}
		return res;
	}
	
	public Item delete(User user){
		return delete(user, true);
	}
	
	public Item delete(User user, boolean log){
		this.deleted = true;
		//Do not need to delete file here, because this is just a soft delete
		this.save();
		if(log){
			Activity.track(this, user, ActivityAction.DELETE);
		}
		return this;
	}
	
	public Item restore(User user){
		return restore(user, true);
	}
	
	public Item restore(User user, boolean log){
		this.deleted = false;
		this.save();
		if(log){
			Activity.track(this, user, ActivityAction.RESTORE);
		}
		return this;
	}
	
	public Item update(User user, boolean isCheck){
		this.createSmartInput();
		this.save();
		if( !isCheck ){
			Activity.track(this, user, ActivityAction.CHANGE);
		}else{
			Activity.track(this, user, this.checkbox ? ActivityAction.CHECK : ActivityAction.UNCHECK);
		}
		return this;
	}	

	public String toXML(){
		String xml = "<item>";
		List<ItemField> itemFields = Item.getItemFields();
		for(ItemField f : itemFields){
			if( this.hasData(f.fieldName) ){
				xml += "<" + f.fieldName + ">" + this.getValueOfField(f.fieldName) +"</" + f.fieldName + ">";
			}
		}
		xml += "</item>";
		return xml;
	}
	
	public static List<Item> findDeleted(Project p){
		org.hibernate.Filter deleteFilter = ((Session)JPA.em().getDelegate()).getEnabledFilter("deleted");
		deleteFilter.setParameter("deleted", true);
		List<Item> items = Item.find("project = ? ORDER BY updated DESC", p).fetch();
		deleteFilter.setParameter("deleted", false);
		return items;
	}

	@Override
	public Project getProject() {
		return this.project;
	}

	@Override
	public String getLogName() {
		return this.name;
	}
	
	@Override
	public String getActivityShowLink() {
		Map<String, Object> args = new HashMap<String, Object>();
		args.put("item_id", id);
		return Router.getFullUrl("Items.show", args);
	}
}
