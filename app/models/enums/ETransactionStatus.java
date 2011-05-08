package models.enums;

public enum ETransactionStatus {
	PLANNED("planned"),
	PAID("paid"),
	CANCELLED("cancelled");
	
	private final String name;
	private ETransactionStatus(String name) {
		this.name = name;
	}
	public String getName(){
		return name;
	}
}
