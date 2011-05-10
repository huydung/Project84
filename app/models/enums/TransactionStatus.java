package models.enums;

public enum TransactionStatus {
	PLANNED("planned"),
	PAID("paid"),
	CANCELLED("cancelled");
	
	private final String name;
	private TransactionStatus(String name) {
		this.name = name;
	}
	public String getName(){
		return name;
	}
}
