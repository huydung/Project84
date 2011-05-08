package models.fields;

public class LocationField extends BasicField {
	
	private String lattitude;
	private String longitude;
	private String address;
	
	public LocationField(String address, String lattitude, String longitude) {
		super("[" + lattitude + "," + longitude + "] "+ address);
		this.lattitude = lattitude;
		this.longitude = longitude;
		this.address = address;
	}
	
	@Override
	public String getValue() {
		return address;
	}
	
	public String getLattitude() {
		return lattitude;
	}

	public void setLattitude(String lattitude) {
		this.lattitude = lattitude;
	}

	public String getLongitude() {
		return longitude;
	}

	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	
}
