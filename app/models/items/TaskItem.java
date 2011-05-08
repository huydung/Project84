package models.items;

import java.util.Date;

import javax.persistence.Embedded;

import models.fields.DateField;
import net.sf.oval.constraint.Min;
import play.data.validation.InFuture;
import play.data.validation.Max;
import play.data.validation.Required;

public class TaskItem extends BasicItem {
	@Required @Max(5) @Min(1)
	public int priority = 3;
	
	public boolean checked = false;
	@InFuture
	public Date dueDate;
	
}
