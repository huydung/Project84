package models;

import play.*;
import play.data.validation.Required;
import play.db.jpa.*;

import javax.persistence.*;
import java.util.*;

@Entity
public class Membership extends Model {
    
	public String userEmail;
	public User user;
	
	@Required
	public Project project;
	
    public String roles;
}
