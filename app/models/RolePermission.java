package models;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;

import org.apache.commons.lang.StringUtils;
import org.hibernate.annotations.CascadeType;
import org.hibernate.engine.Cascade;

import models.enums.PermissionKey;
import models.enums.Role;
import play.db.jpa.Model;

@Entity
public class RolePermission extends Model {
	@ManyToOne
	public Project project;
	@Enumerated(EnumType.STRING)
	public Role role;
	@Lob
	public String permissions;
	
	public RolePermission(Project project, Role role, String permissions) {
		super();
		this.project = project;
		this.role = role;
		this.permissions = permissions;
	}

	public boolean check(Role role, String key){
		if( this.role.equals(role) ){
			return this.permissions.toLowerCase().contains(key.toLowerCase());
		}
		return false;
	}
	
	public void savePermissions(String[] permissions){
		this.permissions = StringUtils.join(permissions, ",");
		this.save();
	}
}
