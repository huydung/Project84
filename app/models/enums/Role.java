package models.enums;

public enum Role {
	ADMIN("admin"),
	MEMBER("member"),
	CLIENT("client");
	
	private final String name;
	
	private Role(String name) {
		this.name = name;
	}
	
	public String getName(){
		return name;
	}
	
	public static Role parse(String name){
		Role role = null;
		for( Role r : Role.values() ){
			if( r.getName().equals(name) ){
				role = r;
				break;
			}
		}
		return role;
	}
}
