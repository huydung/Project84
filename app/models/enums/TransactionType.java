package models.enums;

public enum TransactionType {
	EXPENSE("expense", -1, "spend"),
	INCOME("income", 1, "receive"),
	DEBT("debt", -1, "return"),
	LOAN("loan", 1, "get back"),
	ITEM("item", -1, "buy"),
	SERVICE("service", -1, "pay for");
	
	private final String name;
	private final String taskAction;
	private TransactionType(String name, int factor, String taskAction) {
		this.name = name;
		this.taskAction = taskAction;
	}
	public String getName(){
		return name;
	}
}
