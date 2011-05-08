package models.fields;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;

import org.apache.commons.lang.time.DateUtils;


public class DateField extends BasicField {

	public DateField(Date data, String name) {
		
		super(new Long(data.getTime()).toString());
		if(name == null){ name = "Due Date"; }
		this.name = name;
	}

	@Override
	public Date getValue(){
		try{
			return new Date( new Long(data) );
		}catch(Exception e){
			return null;
		}
	}

	@Override
	public String toString() {
		DateFormat formatter = DateFormat.getDateInstance();
		Date d = getValue();
		if(d == null){
			return name + ": undefined" ;
		}else{
			return name + ": " + formatter.format(getValue());
		}		
	}
	
	public int getDaysTillDate(){
		Date now = new Date();
		return (int)Math.floor((getValue().getTime() - now.getTime())/DateUtils.MILLIS_PER_DAY);
	}
}
