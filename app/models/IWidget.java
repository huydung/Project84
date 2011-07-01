package models;

import java.util.List;
import java.util.Map;

import com.huydung.utils.Link;

public interface IWidget {
	public String getWidgetName();
	public String getSubName();
	public String getHtmlClass();
	public Link getFirstLink();
	public Link getLastLink();
	public List getItems();
	public String getHtml();
	public int getColSpan();
}
