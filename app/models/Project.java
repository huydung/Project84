package models;

import play.*;
import play.db.jpa.*;

import javax.persistence.*;

import models.items.BasicItem;

import java.util.*;

@Entity
public class Project extends BasicItem {
    public String name;
    public String description;
    public ArrayList<Tab> tabs;
    public Date dueDate;
    

}
