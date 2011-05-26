package models.enums;

public enum ActivityType{
	MESSAGE("message"),
	CHANGE("change"),
	COMMENT("comment"),
	LIST("list"),
	ITEM("item")	;
	
	private final String name;
	private ActivityType(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
	
	public static ActivityType parse(String name){
		ActivityType type = null;
		for( ActivityType r : ActivityType.values() ){
			if( r.getName().equals(name) ){
				type = r;
				break;
			}
		}
		return type;
	}
}