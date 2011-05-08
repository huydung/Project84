package models.items;

import javax.persistence.Embedded;

import play.data.validation.URL;

import models.fields.UrlField;

public class LinkItem extends BasicItem {
	@URL
	public String url;
}	
