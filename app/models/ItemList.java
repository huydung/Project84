package models;

import java.util.ArrayList;

import models.fields.BasicField;

import play.db.jpa.Model;

public class ItemList extends Model {
	public String name;
	public boolean folder = true;
	public String sid;
	public String fieldsConfiguration;
}
