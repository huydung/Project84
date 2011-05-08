package models.fields;

import java.net.MalformedURLException;
import java.net.URL;

public class UrlField extends BasicField{
	@Override
	public String getValue() {
		return data;
	}

	public UrlField(String data, String name) {
		super(data);
		try {
			URL url = new URL(data);
			this.data = data;			
		} catch (MalformedURLException e) {
			//TASK: create a default URL placeholder for invalid URL
			this.data = "http://google.com";
		}
	}
	
}
