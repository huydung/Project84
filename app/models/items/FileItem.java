package models.items;

import javax.persistence.Embedded;

import play.data.validation.Required;
import play.data.validation.URL;

import models.fields.UrlField;

public class FileItem extends BasicItem {
	@Required @URL
	public String fileUrl;
	public long size;
	public int width;
	public int height;
	@URL
	public String thumbUrl;
}
