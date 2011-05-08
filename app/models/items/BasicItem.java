package models.items;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;

import models.User;

import play.db.jpa.Model;


public class BasicItem extends Model {
	public Date created;
	public User creator;
	public Date updated;
	public String type;
	public String name;
	public String folder;
}
