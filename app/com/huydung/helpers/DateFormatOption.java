package com.huydung.helpers;

public class DateFormatOption {
	private String id;
	private String label;
	
	
	public DateFormatOption(String id, String label) {
		super();
		this.id = id;
		this.label = label;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	@Override
	public String toString() {
		return label;
	}
	
	/** 
	 * See more: http://docs.jquery.com/UI/Datepicker/formatDate
	 * @return
	 */
	public static String toJsString(String format){
		if( format.contains("MMMMM") ){
			format = format.replace("MMMMM", "MM");
		}else{
			if( format.contains("MMM") ){
				format = format.replace("MMM", "M");
			}else{
				format = format.replace("MM", "mm");
			}			
		}
		if( format.contains("yyyy") ){
			format = format.replace("yyyy", "yy");
		}else{
			format = format.replace("yy", "y");
		}
		return format;
	}
}
