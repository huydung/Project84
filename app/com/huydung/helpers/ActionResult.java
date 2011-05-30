package com.huydung.helpers;

public class ActionResult {
	private boolean success = true;
	private boolean warning = false;
	private Object data;
	private String message;
	
	public ActionResult(boolean success) {
		this.success = success;
	}
	
	public ActionResult(boolean success, String message) {
		this(success);
		this.message = message;
	}
	
	public ActionResult(boolean success, String message, Object data) {
		this(success, message);
		this.data = data;
	}
	
	public ActionResult(
		boolean success, String message, boolean isWarning
	) {
		this(success, message);
		this.warning = isWarning;
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

	public Object getData() {
		return data;
	}

	public void setData(Object data) {
		this.data = data;
	}
	
}
