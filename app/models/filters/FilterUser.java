package models.filters;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.huydung.utils.ItemField;
import com.huydung.utils.MiscUtil;

import models.Listing;
import models.Membership;
import models.User;
import play.mvc.Scope.Params;

public class FilterUser extends BasicFilter {

	private List<User> users;
	public FilterUser(ItemField field, Listing listing) {
		super(field, listing);
		users = Membership.findUserByProject(listing.project);
	}
	
	public List<User> getUsers(){
		if(users == null){
			users = Membership.findUserByProject(listing.project);
			MiscUtil.ConsoleLog("Fetch list of users in UserFilter");
		}		
		return users;
	}
	
	@Override
	public void setDefault(Params params) {
		if( users != null ){
			for( User u : users ){
				params.put("filter_user_" + u.id, "on");
			}
		}else{
			MiscUtil.ConsoleLog("WTF, users is null?");
		}
	}

	@Override
	public String getJPQL(Params params) {
		if( users != null ){
			boolean selectedAll = true;
			List<Long> ids = new ArrayList<Long>();
			for( User u : users ){				
				String s = params.get("filter_user_" + u.id);
				if( s != null && s.equals("on") ){
					ids.add(u.id);			
				} else {
					selectedAll = false;
				}				
			}
			if( selectedAll || ids.size() == 0){
				return "";
			}else{
				return " user_id IN ("+ StringUtils.join(ids, ",") +") ";
			}
		}else{
			MiscUtil.ConsoleLog("WTF, users is null?");
		}
		return "";
	}

	@Override
	public String getIncludeFile() {
		return "items/filters/user.html";
	}

}
