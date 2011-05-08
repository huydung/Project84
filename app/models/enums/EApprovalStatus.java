package models.enums;

public enum EApprovalStatus {
	ACCEPTED("accepted"),
	UNREAD("unread"),
	PENDING("pending"),
	DENIED("denied");
	
	private final String name;
	private EApprovalStatus(String name) {
		this.name = name;
	}
	public String getName(){
		return name;
	}
}
