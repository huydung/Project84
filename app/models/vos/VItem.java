package models.vos;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import models.Item;

public class VItem {
	public String name;
	private Map<String, String> map;
	
	public String getValueOfField(String field){
		return map.get(field);
	}
	
	public VItem(Item item){
		this.map = item.fields_data;
		this.name = item.name;
	}
	
	public static List<VItem> createVItems(List<Item> items){
		List<VItem> vis = new ArrayList<VItem>();
		for(Item i:items){
			vis.add(new VItem(i));
		}
		return vis;
	}
}
