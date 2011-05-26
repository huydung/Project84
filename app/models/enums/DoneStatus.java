package models.enums;

public enum DoneStatus {
	COMPLETED("completed"),
	LATE("late"),
	WAITING("waiting"),
	ABANDONED("abandonned"),
	ONGOING("ongoing"),
	ARCHIVED("archived");
	
	private final String name;
	private DoneStatus(String name) {
		this.name = name;
	}
	public String getName(){
		return name;
	}
	
	public static DoneStatus parse(String name){
		DoneStatus d = null;
		for( DoneStatus r : DoneStatus.values() ){
			if( r.getName().equals(name) ){
				d = r;
				break;
			}
		}
		return d;
	}
}
