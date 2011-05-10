package com.huydung.helpers;

public class ActionResult {
	private boolean success = true;
	private boolean warning = false;
	private String message;
	
	public ActionResult(boolean success, String message) {
		super();
		this.success = success;
		this.message = message;
	}
	
	public ActionResult(boolean success, String message, boolean isWaning) {
		this(success, message);
		this.warning = isWaning;
	}
	
	public boolean isSuccess() {
		return success;
	}
	public void setSuccess(boolean success) {
		this.success = success;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}

	public boolean isWarning() {
		return warning;
	}

	public void setWarning(boolean warning) {
		this.warning = warning;
	}
	
}
