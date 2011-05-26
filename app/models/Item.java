package models;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.Date;

import javax.persistence.Lob;

import models.enums.ItemType;

import play.data.validation.MaxSize;
import play.data.validation.Required;
import play.data.validation.URL;

public class Item extends BasicItem{
	
	public Item(ItemType type) {
		super();
		this.type = type.getName();
	}

	@MaxSize(255)
	public String description;
	
	@Lob
	@MaxSize(4000)
	public String body;
	
	/* Simple Attributes */
	public Date date; //Can either be Due Date or Date	
	public User assignedTo;
	public String category;
	@URL
	public String url;
	
	/* Contact Related Attributes */	
	public String phone1;
	public String phone2;
	public String email1;
	public String email2;
	
	/* Address Related Attributes */
	public String address;
	public Long lat;
	public Long lan;
	
	/* File Attributes */
	public String filepath;
	public String mimeType;
	
	/* Asset Attributes */
	public BigDecimal price;
	public Integer amount;
	public String currency;
	
}
