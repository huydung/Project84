package models;

import java.util.List;
import java.util.Map;

import com.huydung.utils.Link;

public interface IWidget {
	public String getName();
	public String getSubName();
	public String getHtmlClass();
	public Link getFirstLink();
	public Link getLastLink();
	public List getItems(Long project_id);
	public String getHtml();
	public int getColSpan();
}
