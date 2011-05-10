package models.enums;

public enum ApprovalStatus {
	ACCEPTED("accepted"),
	UNREAD("unread"),
	WAITING_INVITE("inviting"),
	DENIED("denied");
	
	private final String name;
	
	private ApprovalStatus(String name) {
		this.name = name;
	}
	
	public String getName(){
		return name;
	}
	
	public static ApprovalStatus parse(String name){
		ApprovalStatus status = null;
		for( ApprovalStatus s : ApprovalStatus.values() ){
			if( s.getName().equals(name) ){
				status = s;
				break;
			}
		}
		return status;
	}
}
