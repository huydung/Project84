package models;

import play.*;
import play.data.validation.MaxSize;
import play.data.validation.Required;
import play.db.jpa.*;

import javax.persistence.*;


import java.util.*;

@Entity
public class Comment extends BasicItem {
	
	@Lob
	@Required
	@MaxSize(4000)
	public String body;
	
	@Required
	public Long forId;
	
	@Required
	public String forType;
	
	public Comment() {
		super();
		this.type = "comment";
	}	
     
}
