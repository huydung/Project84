package models.features;

import java.util.List;

import models.Item;
import models.Listing;

public abstract class BasicFeature {
	protected List<Item> items;
	protected String filterString = null;
	protected Listing listing;
	protected String widgetTemplate;
	protected boolean isModal = false;
	protected String result = ""; 
	protected String name = "";
	protected String identifier = "";
	protected boolean isActive = false;
	
	public BasicFeature(String widgetTemplate) {
		super();
		this.widgetTemplate = widgetTemplate;
	}
	
	public BasicFeature(String widgetTemplate, String filterString) {
		this(widgetTemplate);
		this.filterString = filterString;
	}
			
	public boolean isActive() {
		return isActive;
	}

	public void setActive(boolean isActive) {
		this.isActive = isActive;
	}

	public String getIdentifier() {
		return identifier;
	}

	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}

	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}

	public Listing getListing() {
		return listing;
	}

	public void setListing(Listing listing) {
		this.listing = listing;
	}

	public String getFilterString() {
		return filterString;
	}

	public void setFilterString(String filterString) {
		this.filterString = filterString;
	}

	public List<Item> getItems() {
		return items;
	}

	public void setItems(List<Item> items) {
		this.items = items;
	}

	public String getWidgetTemplate() {
		return widgetTemplate;
	}

	public boolean isModal() {
		return isModal;
	}

	public abstract void process();
	public abstract String getResult();
}