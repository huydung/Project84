package models.items;

import play.data.validation.Email;
import play.data.validation.Required;
import play.data.validation.URL;

public class ContactItem extends BasicItem {
	@Required
	public String realName;
	@Email
	public String email;
	@URL
	public String website;
	
	public String tel;
	
	public String address;
	public long lattitude;
	public long longitude;
}
