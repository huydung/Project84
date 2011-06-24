package models.enums;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import models.Listing;
import models.Project;

import org.apache.commons.lang.StringUtils;

import com.huydung.utils.PermConfig;

public enum PermissionKey {
	
	/** IMPORTANT: Everytime you try to add/edit/delete Keys in this class,
	 * Take a look at other methods in this too.
	 */
	
	/** MEMBERSHIPS **/
	EDIT_MEMBERSHIPS,
	EDIT_OWN_MEMBERSHIPS,
	EDIT_USERS_PERMISSIONS,
	CREATE_INVITATIONS,
	DELETE_MEMBERSHIPS,
	DELETE_OWN_MEMBERSHIPS,
	
	/** PROJECT **/
	EDIT_PROJECT_INFO,
	CHANGE_PROJECT_STATUS,
	
	/** LISTING **/
	VIEW_ITEMS,
	LISTING_CONFIG,
	DELETE_LISTING,
	CREATE_ITEM,
	EDIT_ITEM,
	EDIT_OWN_ITEM,
	DELETE_ITEM,
	DELETE_OWN_ITEM,
	CHECK_ITEM,
	APPROVE_ITEM,
	CREATE_COMMENT,
	DELETE_COMMENT,
	DELETE_OWN_COMMENT,
	UPLOAD_FILE;
	
	public static String getDefaultPermissions(Role role){
		List<PermissionKey> keys = new ArrayList<PermissionKey>();
		switch(role){
			case ADMIN:{
				keys.add(EDIT_MEMBERSHIPS);
				keys.add(EDIT_USERS_PERMISSIONS);
				keys.add(CREATE_INVITATIONS);
				keys.add(DELETE_MEMBERSHIPS);
				keys.add(EDIT_PROJECT_INFO);
				keys.add(CHANGE_PROJECT_STATUS);
				return StringUtils.join(keys, ",");
			}
			case POWER_MEMBER:{		
				keys.add(CREATE_INVITATIONS);
				keys.add(EDIT_OWN_MEMBERSHIPS);
				keys.add(DELETE_OWN_MEMBERSHIPS);
				return StringUtils.join(keys, ",");						
			}
		}
		return "";
	}
	
	public static String getDefaultListingPermissions(Role role, Long id){
		List<String> keys = new ArrayList<String>();
		switch(role){
			case ADMIN:{
				keys.add(VIEW_ITEMS + "_" + id);
				keys.add(LISTING_CONFIG + "_" + id);
				keys.add(DELETE_LISTING + "_" + id);
				keys.add(CREATE_ITEM + "_" + id);
				keys.add(EDIT_ITEM + "_" + id);
				keys.add(DELETE_ITEM + "_" + id);
				keys.add(CHECK_ITEM + "_" + id);
				keys.add(APPROVE_ITEM + "_" + id);
				keys.add(CREATE_COMMENT + "_" + id);
				keys.add(DELETE_COMMENT + "_" + id);
				keys.add(UPLOAD_FILE + "_" + id);
				return StringUtils.join(keys, ",");
			}
			case POWER_MEMBER:{		
				keys.add(VIEW_ITEMS + "_" + id);
				keys.add(LISTING_CONFIG + "_" + id);
				keys.add(CREATE_ITEM + "_" + id);
				keys.add(EDIT_OWN_ITEM + "_" + id);
				keys.add(DELETE_OWN_ITEM + "_" + id);
				keys.add(CHECK_ITEM + "_" + id);
				keys.add(CREATE_COMMENT + "_" + id);
				keys.add(DELETE_OWN_COMMENT + "_" + id);
				keys.add(UPLOAD_FILE + "_" + id);
				return StringUtils.join(keys, ",");						
			}
			case MEMBER:{		
				keys.add(VIEW_ITEMS + "_" + id);
				keys.add(CREATE_ITEM + "_" + id);
				keys.add(EDIT_OWN_ITEM + "_" + id);
				keys.add(DELETE_OWN_ITEM + "_" + id);
				keys.add(CHECK_ITEM + "_" + id);
				keys.add(CREATE_COMMENT + "_" + id);
				keys.add(DELETE_OWN_COMMENT + "_" + id);
				return StringUtils.join(keys, ",");						
			}
			case CLIENT:{		
				keys.add(VIEW_ITEMS + "_" + id);
				keys.add(CREATE_COMMENT + "_" + id);
				keys.add(DELETE_OWN_COMMENT + "_" + id);
				keys.add(APPROVE_ITEM + "_" + id);
				return StringUtils.join(keys, ",");						
			}
		}
		return "";
	}
	
	public static LinkedHashMap<String, List<PermConfig>> getPermissions(Project p){
		LinkedHashMap<String, List<PermConfig>> perms = new LinkedHashMap<String, List<PermConfig>>();
		
		//Project
    	ArrayList<PermConfig> pl = new ArrayList<PermConfig>();
    	pl.add(new PermConfig(PermissionKey.EDIT_PROJECT_INFO, null, p));
    	pl.add(new PermConfig(PermissionKey.CHANGE_PROJECT_STATUS, null, p));
    	perms.put("Project", pl);
    	
    	//Membership
    	ArrayList<PermConfig> m = new ArrayList<PermConfig>();
    	m.add(new PermConfig(PermissionKey.EDIT_MEMBERSHIPS, null, p));
    	m.add(new PermConfig(PermissionKey.EDIT_OWN_MEMBERSHIPS, null, p));
    	m.add(new PermConfig(PermissionKey.EDIT_USERS_PERMISSIONS, null, p));
    	m.add(new PermConfig(PermissionKey.CREATE_INVITATIONS, null, p));
    	m.add(new PermConfig(PermissionKey.DELETE_MEMBERSHIPS, null, p));
    	m.add(new PermConfig(PermissionKey.DELETE_OWN_MEMBERSHIPS, null, p));
    	perms.put("Membership", m);
    	
    	//Listings
    	for( Listing listing : p.listings ){
    		ArrayList<PermConfig> l = new ArrayList<PermConfig>();
    		l.add(new PermConfig(PermissionKey.VIEW_ITEMS, listing.id, p));
        	l.add(new PermConfig(PermissionKey.LISTING_CONFIG, listing.id, p));
        	l.add(new PermConfig(PermissionKey.CREATE_ITEM, listing.id, p));
        	l.add(new PermConfig(PermissionKey.EDIT_ITEM, listing.id, p));
        	l.add(new PermConfig(PermissionKey.EDIT_OWN_ITEM, listing.id, p));
        	l.add(new PermConfig(PermissionKey.DELETE_ITEM, listing.id, p));
        	l.add(new PermConfig(PermissionKey.DELETE_OWN_ITEM, listing.id, p));
        	l.add(new PermConfig(PermissionKey.CHECK_ITEM, listing.id, p));        	
        	l.add(new PermConfig(PermissionKey.APPROVE_ITEM, listing.id, p));
        	l.add(new PermConfig(PermissionKey.CREATE_COMMENT, listing.id, p));
        	l.add(new PermConfig(PermissionKey.DELETE_COMMENT, listing.id, p));
        	l.add(new PermConfig(PermissionKey.DELETE_OWN_COMMENT, listing.id, p));
        	l.add(new PermConfig(PermissionKey.UPLOAD_FILE, listing.id, p));
        	l.add(new PermConfig(PermissionKey.DELETE_LISTING, listing.id, p));
        	perms.put("Listing " + listing.listingName , l);
    	}
   	
    	return perms;
    }
}
