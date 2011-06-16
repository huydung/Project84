package models.enums;

import java.util.ArrayList;
import java.util.List;

public enum Role {
	ADMIN,
	POWER_MEMBER,
	MEMBER,
	
	CLIENT;

	public static Role parse(String name){
		Role role = null;
		for( Role r : Role.values() ){
			if( r.toString().toLowerCase().equals(name.toLowerCase()) ){
				role = r;
				break;
			}
		}
		return role;
	}
	
    public static List<Role> getRoles(){
    	Role[] rs = Role.values();
    	ArrayList<Role> roles = new ArrayList<Role>();
    	for( Role r : rs ){
    		roles.add(r);
    	}
    	return roles;
    }
}
