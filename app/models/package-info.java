@FilterDef(name="deleted", parameters = { 
	@ParamDef(name="deleted",type="boolean") 
}, defaultCondition = ":deleted = deleted")
package models; 
import org.hibernate.annotations.FilterDef;
import org.hibernate.annotations.ParamDef;