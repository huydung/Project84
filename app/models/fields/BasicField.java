package models.fields;

import java.text.ParseException;

import javax.persistence.Embeddable;

import play.db.jpa.GenericModel;
import play.db.jpa.Model;
@Embeddable
public class BasicField extends GenericModel{
	public String data;
	public String name = "Custom Field";
	
	public BasicField(String data){
		this.data = data;
	}
	
	public String toString(){
		return name + ": " + data;
	}
	
	public <T> T getValue(){
		return null;
	};
}
