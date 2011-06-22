package units;

import static org.junit.Assert.*;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import models.Item;
import models.Listing;

import org.junit.Before;
import org.junit.Test;

import play.templates.JavaExtensions;
public class ItemParserTest {
	
	private Listing l;
	
	@Before
	public void setUp(){
		l = new Listing("Tasks");	
	}
	
	@Test
	public void testItemParseNumber(){
		Item item = Item.createFromSmartInput("Viết test unit num:3", l);
		assertEquals(new Integer(3), item.number);
		assertEquals("Viết test unit", item.name);
		
		item = Item.createFromSmartInput("num:4 Viết test unit lần 2", l);
		assertEquals(new Integer(4), item.number);
		assertEquals("Viết test unit lần 2", item.name);
		
		item = Item.createFromSmartInput("Viết test unit lần 3 num:40 5", l);
		assertEquals(new Integer(40), item.number);
		assertEquals("Viết test unit lần 3 5", item.name);
		
		item = Item.createFromSmartInput("Viết test unit lần 4 num:40 num:5", l);
		assertEquals(new Integer(5), item.number);
		assertEquals("Viết test unit lần 4", item.name);
	}
	
	@Test
	public void testItemParseEmail(){
		Item item = Item.createFromSmartInput("Viết test unit vannessars@yahoo.com", l);
		assertEquals("vannessars@yahoo.com", item.email1);
		assertEquals(null, item.email2);
		assertEquals("Viết test unit", item.name);
		
		item = Item.createFromSmartInput("Viết test unit vannessars@yahoo.com.vn oakman.hd@gmail.com", l);
		assertEquals("vannessars@yahoo.com.vn", item.email1);
		assertEquals("oakman.hd@gmail.com", item.email2);
		assertEquals("Viết test unit", item.name);
		
		item = Item.createFromSmartInput("hd@madeby84.com Viết test unit lần 2 vannessars@yahoo.com", l);
		assertEquals("hd@madeby84.com", item.email1);
		assertEquals("vannessars@yahoo.com", item.email2);		
		assertEquals("Viết test unit lần 2", item.name);
		
		item = Item.createFromSmartInput("oakman.hd@gmail.com Viết test unit lần 2 vannessars@yahoo.com contact.huydung@gmail.com", l);		
		assertEquals("vannessars@yahoo.com", item.email1);
		assertEquals("contact.huydung@gmail.com", item.email2);
		assertEquals("Viết test unit lần 2", item.name);
		
		item = Item.createFromSmartInput("Viết test unit vannessars@yahoo", l);
		assertEquals(null, item.email1);
		assertEquals(null, item.email2);
		assertEquals("Viết test unit vannessars@yahoo", item.name);
		
		item = Item.createFromSmartInput("Viết test unit vannessars.@yahoo.com", l);
		assertEquals(null, item.email1);
		assertEquals("Viết test unit vannessars.@yahoo.com", item.name);
	}
	
	@Test
	public void testItemParseCost(){
		Item item = Item.createFromSmartInput(
				"Buy hosting USD 250x3", l);
		assertEquals(new BigDecimal(250), item.cost);
		assertEquals(new Integer(3), item.cost_amount);
		assertEquals("USD", item.cost_currency);
		assertEquals("Buy hosting", item.name);
		
		item = Item.createFromSmartInput(
				"Buy hosting USD1250", l);
		assertEquals(new BigDecimal(1250), item.cost);
		assertEquals(new Integer(1), item.cost_amount);
		assertEquals("USD", item.cost_currency);
		assertEquals("Buy hosting", item.name);
		
		item = Item.createFromSmartInput(
				"Buy hosting 1250", l);
		assertEquals(new BigDecimal(1250), item.cost);
		assertEquals(new Integer(1), item.cost_amount);
		assertEquals("VND", item.cost_currency);
		assertEquals("Buy hosting", item.name);
		
		item = Item.createFromSmartInput(
				"Buy hosting 1250x5", l);
		assertEquals(new BigDecimal(1250), item.cost);
		assertEquals(new Integer(5), item.cost_amount);
		assertEquals("VND", item.cost_currency);
		assertEquals("Buy hosting", item.name);
		
		item = Item.createFromSmartInput(
				" SGD 125.25x15 Buy hosting", l);
		assertEquals(new BigDecimal(125.25), item.cost);
		assertEquals(new Integer(15), item.cost_amount);
		assertEquals("SGD", item.cost_currency);
		assertEquals("Buy hosting", item.name);
		
		item = Item.createFromSmartInput(
				"Buy hosting SgD 125.25x15", l);
		assertEquals(new BigDecimal(125.25), item.cost);
		assertEquals(new Integer(15), item.cost_amount);
		assertEquals("SGD", item.cost_currency);
		assertEquals("Buy hosting", item.name);
		
		item = Item.createFromSmartInput(
				"Buy hosting XXX 125.25x15", l);
		assertEquals(null, item.cost);
		assertEquals(null, item.cost_amount);
		assertEquals(null, item.cost_currency);
		assertEquals("Buy hosting XXX 125.25x15", item.name);
		
		item = Item.createFromSmartInput(
				"Buy hosting SGD VND 125000000X10", l);
		assertEquals(new BigDecimal(125000000), item.cost);
		assertEquals(new Integer(10), item.cost_amount);
		assertEquals("VND", item.cost_currency);
		assertEquals("Buy hosting SGD", item.name);
		
		item = Item.createFromSmartInput(
				"Buy hosting USD 18X100 VND 125000000x10", l);
		assertEquals(new BigDecimal(125000000), item.cost);
		assertEquals(new Integer(10), item.cost_amount);
		assertEquals("VND", item.cost_currency);
		assertEquals("Buy hosting", item.name);
	}
	
	@Test
	public void testItemParseTelephone(){
		Item item = Item.createFromSmartInput(
				"Nguyễn Huy Dũng tel:0984903707", l);
		assertEquals("0984903707", item.phone1);
		assertEquals(null, item.phone2);
		assertEquals("Nguyễn Huy Dũng", item.name);
		
		item = Item.createFromSmartInput(
				"Nguyễn Huy Dũng tel:0984903707,0985898138", l);
		assertEquals("0984903707", item.phone1);
		assertEquals("0985898138", item.phone2);
		assertEquals("Nguyễn Huy Dũng", item.name);
		
		item = Item.createFromSmartInput(
				"Nguyễn Huy Dũng tel:0984903707,0985898138,09123456", l);
		assertEquals("0985898138", item.phone1);
		assertEquals("09123456", item.phone2);
		assertEquals("Nguyễn Huy Dũng", item.name);
		
		item = Item.createFromSmartInput(
				"tel:0985898137 Nguyễn Huy Dũng tel:0984903707 ", l);
		assertEquals("0985898137", item.phone1);
		assertEquals("0984903707", item.phone2);
		assertEquals("Nguyễn Huy Dũng", item.name);
		
	}
	
	@Test
	public void testParseDate(){
		Calendar cal = new GregorianCalendar();
		cal.setTimeZone(TimeZone.getDefault());
		cal.set(2011, 5, 14); //2011-06-14
		cal.getTime();//Trigger the above setting to correct fields like WEEK_OF_MONTH
		SimpleDateFormat sdf= new SimpleDateFormat("yyyy-MM-dd");
		sdf.setTimeZone(TimeZone.getDefault());
				
		Item item = Item.createFromSmartInput("Do something @tomorrow", l, cal);
		assertNotNull(item.date);
		assertEquals("2011-06-15",sdf.format(item.date));
		assertEquals("Do something", item.name);
		
		item = Item.createFromSmartInput("Do something @yesterday", l, cal);
		assertNotNull(item.date);
		assertEquals("2011-06-13",sdf.format(item.date));
		assertEquals("Do something", item.name);
		
		item = Item.createFromSmartInput("Do something @next 3 days", l, cal);
		assertNotNull(item.date);
		assertEquals("2011-06-17",sdf.format(item.date));
		assertEquals("Do something", item.name);
		
		item = Item.createFromSmartInput("Do something @-4 days", l, cal);
		assertNotNull(item.date);
		assertEquals("2011-06-10",sdf.format(item.date));
		assertEquals("Do something", item.name);
		
		item = Item.createFromSmartInput("Do something @+2day", l, cal);
		assertNotNull(item.date);
		assertEquals("2011-06-16",sdf.format(item.date));
		assertEquals("Do something", item.name);
		
		item = Item.createFromSmartInput("Do something @last 2 day", l, cal);
		assertNotNull(item.date);
		assertEquals("2011-06-12",sdf.format(item.date));
		assertEquals("Do something", item.name);
		
		item = Item.createFromSmartInput("Do something @next week", l, cal);
		assertNotNull(item.date);
		//Note: Default Start of Week is Monday
		assertEquals("2011-06-20",sdf.format(item.date));
		assertEquals("Do something", item.name);
		
		item = Item.createFromSmartInput("Do something @last week", l, cal);
		assertNotNull(item.date);
		//Note: Default Start of Week is Monday
		assertEquals("2011-06-06",sdf.format(item.date));
		assertEquals("Do something", item.name);
		
		item = Item.createFromSmartInput("Do something @next wed", l, cal);
		assertNotNull(item.date);
		assertEquals("2011-06-15",sdf.format(item.date));
		assertEquals("Do something", item.name);
		
		item = Item.createFromSmartInput("Do something @sunday num:4", l, cal);
		assertNotNull(item.date);
		assertEquals("2011-06-19",sdf.format(item.date));
		assertEquals(new Integer(4),item.number);
		assertEquals("Do something", item.name);
		
		item = Item.createFromSmartInput("Do something @last Thursday", l, cal);
		assertNotNull(item.date);
		assertEquals("2011-06-09",sdf.format(item.date));
		assertEquals("Do something", item.name);

		item = Item.createFromSmartInput("Do something @19/06", l, cal);
		assertNotNull(item.date);
		assertEquals("2011-06-19",sdf.format(item.date));
		assertEquals("Do something", item.name);

		item = Item.createFromSmartInput("Do something @05.06.2012", l, cal);
		assertEquals("2012-06-05",sdf.format(item.date));
		assertEquals("Do something", item.name);
		
		item = Item.createFromSmartInput("Do something @1987-06-14", l, cal);
		assertEquals("1987-06-14",sdf.format(item.date));
		assertEquals("Do something", item.name);		

		item = Item.createFromSmartInput("Do something @17-06", l, cal);	
		assertNotNull(item.date);
		assertEquals("2011-06-17",sdf.format(item.date));
		assertEquals("Do something", item.name);
		
		item = Item.createFromSmartInput("Do something @June 18", l, cal);
		assertNotNull(item.date);
		assertEquals("2011-06-18",sdf.format(item.date));
		assertEquals("Do something", item.name);
		
		item = Item.createFromSmartInput("Do something @7 Oct", l, cal);
		assertNotNull(item.date);
		assertEquals("2011-10-07",sdf.format(item.date));
		assertEquals("Do something", item.name);
	}
	
	@Test
	public void testItemParseAddress(){
		Item item = Item.createFromSmartInput(
				"Nguyễn Huy Dũng add:17 Quán Thánh, Ba Đình, Hà Nội#", l);
		assertEquals("17 Quán Thánh, Ba Đình, Hà Nội", item.address);
		assertEquals("Nguyễn Huy Dũng", item.name);
		
		item = Item.createFromSmartInput(
				"add:138/191 ngõ Văn Chương, Tôn Đức Thắng, Hà Nội# Nguyễn Huy Dũng ", l);
		assertEquals("138/191 ngõ Văn Chương, Tôn Đức Thắng, Hà Nội", item.address);
		assertEquals("Nguyễn Huy Dũng", item.name);
		
		item = Item.createFromSmartInput(
				"Nguyễn Huy Dũng add:138/191 ngõ Văn Chương, Tôn Đức Thắng, Hà Nội# tel:0984903707,01234567 contact.huydung@gmail.com", l);
		assertEquals("138/191 ngõ Văn Chương, Tôn Đức Thắng, Hà Nội", item.address);
		assertEquals("0984903707", item.phone1);
		assertEquals("01234567", item.phone2);
		assertEquals("contact.huydung@gmail.com", item.email1);
		assertEquals("Nguyễn Huy Dũng", item.name);
		
	}
	
	@Test
	public void testItemParseCategory(){
		Item item = Item.createFromSmartInput(
				"Nguyễn Huy Dũng <category 1>", l);
		assertEquals("category 1", item.category);
		assertEquals("Nguyễn Huy Dũng", item.name);
				
	}
	
	@Test
	public void testItemParseAll(){
		Item item = Item.createFromSmartInput(
				"Buy hosting USD 250x3 num:4", l);
		assertEquals(new BigDecimal(250), item.cost);
		assertEquals(new Integer(3), item.cost_amount);
		assertEquals("USD", item.cost_currency);
		assertEquals("Buy hosting", item.name);
		assertEquals(new Integer(4), item.number);
		
		item = Item.createFromSmartInput(
				"oakman.hd@gmail.com Buy hosting num:4", l);
		assertEquals("oakman.hd@gmail.com", item.email1);
		assertEquals(null, item.email2);
		assertEquals("Buy hosting", item.name);
		assertEquals(new Integer(4), item.number);
	}
}
