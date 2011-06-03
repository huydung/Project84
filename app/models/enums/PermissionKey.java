package models.enums;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

public enum PermissionKey {

	EDIT_MEMBERSHIPS,
	EDIT_OWN_MEMBERSHIPS,
	CREATE_INVITATIONS,
	DELETE_MEMBERSHIPS,
	DELETE_OWN_MEMBERSHIPS,
	EDIT_PROJECT_INFO,
	CHANGE_PROJECT_STATUS,
	CHANGE_PROJECT_LISTS,
	APPROVE_ITEM;
	
	public static String getDefaultPermissions(Role role){
		List<PermissionKey> keys = new ArrayList<PermissionKey>();
		switch(role){
			case ADMIN:{
				return StringUtils.join(PermissionKey.values(), ",");
			}
			case POWER_MEMBER:{		
				keys.add(CREATE_INVITATIONS);
				keys.add(EDIT_OWN_MEMBERSHIPS);
				return StringUtils.join(keys, ",");						
			}
			case MEMBER:{
				return "";
			}
			case CLIENT:{
				keys.add(APPROVE_ITEM);
				return StringUtils.join(keys, ",");		
			}
		}
		return "";
	}
}
