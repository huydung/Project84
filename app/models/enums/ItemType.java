package models.enums;

public enum ItemType {
	FILE("file"),
	EVENT("event"),
	TASK("task"),
	CONTACT("contact"),
	ASSET("asset"),
	CUSTOM("custom");
	
	private final String name;
	private ItemType(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
	
	public static ItemType parse(String name){
		ItemType type = null;
		for( ItemType r : ItemType.values() ){
			if( r.getName().equals(name) ){
				type = r;
				break;
			}
		}
		return type;
	}
}
