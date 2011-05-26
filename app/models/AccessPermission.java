package models;

import java.util.StringTokenizer;

import play.data.validation.Required;
import play.db.jpa.Model;

public class AccessPermission extends Model{
	@Required
	public String role;
	
	@Required
	public String action;
	
	public static boolean check(Membership membership, String action){
		String roles[] = membership.roleNames.split(",");
		for( String role : roles ){
			if( AccessPermission.count("role = ? AND acton = ?", role, action) > 0 ){
				return true;
			};
		}
		return false;
	}
}
