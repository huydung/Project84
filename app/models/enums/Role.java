package models.enums;

public enum Role {
	ADMIN,
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
}
