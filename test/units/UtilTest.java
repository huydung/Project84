package units;

import static org.junit.Assert.*;

import java.util.Calendar;
import java.util.GregorianCalendar;

import org.junit.Before;
import org.junit.Test;

import com.huydung.utils.DateParser;

public class UtilTest {
	private DateParser parser;
	private Calendar base;
	
	@Before
	public void setUp(){
		//the parser will be set base to 23/04/2011
		parser = new DateParser();
		parser.setTimezoneId("GMT+07:00");
		base = new GregorianCalendar(2011, 3, 23);
		parser.setBase(base);
		
	}
	
	@Test
	public void testRelativeDates(){
		assertEquals(
			"2011-24-04 00:00 +0700", 
			parser.parseDateWithFormat("do something @tomorrow")
		);
		parser.setBase(base);
		assertEquals(
			"2011-22-04 00:00 +0700", 
			parser.parseDateWithFormat("do something @yesterday")
		);
		parser.setBase(base);
		assertEquals(
			"2011-29-04 00:00 +0700", 
			parser.parseDateWithFormat("do something @+6 days")
		);
		parser.setBase(base);
		assertEquals(
			"2011-30-04 00:00 +0700", 
			parser.parseDateWithFormat("do something @7 days ahead")
		);
		parser.setBase(base);
		assertEquals(
			"2011-20-04 00:00 +0700", 
			parser.parseDateWithFormat("do something @3 days ago")
		);
		parser.setBase(base);
		assertEquals(
			"2011-24-04 00:00 +0700", 
			parser.parseDateWithFormat("do something @tomorrow")
		);
	}
}
