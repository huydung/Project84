package models.enums;

public enum EDoneStatus {
	COMPLETED("completed"),
	LATE("late"),
	WAITING("waiting"),
	ABANDONED("abandonned"),
	ONGOING("ongoing"),
	ARCHIVED("archived");
	
	private final String name;
	private EDoneStatus(String name) {
		this.name = name;
	}
	public String getName(){
		return name;
	}
}
