package com.huydung.utils;

import java.util.HashMap;

import models.Project;
import models.enums.PermissionKey;
import models.enums.Role;

public class PermConfig {
	public HashMap<Role, Boolean> roles;
	public PermissionKey key;
	public Long listing_id = null;
	
	public PermConfig(PermissionKey key, Long listing_id, Project p) {
		super();
		this.key = key;
		this.listing_id = listing_id;
		getRolesValue(p);
	}
	
	protected void getRolesValue(Project p){
		if( roles == null ){
			roles = new HashMap<Role, Boolean>();
			for( Role r : Role.values() ){
				roles.put(r, 
					p.allow(r, getPermKey())
				);
			}
		}
	}
	
	public Boolean isAllow(Role r){
		if( roles == null ){return false; }
		return roles.get(r);
	}
	
	public String getPermKey(){
		return listing_id == null ? 
				key.toString() : 
				key.toString() + "_" + listing_id;
	}
}
