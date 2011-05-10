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
}
