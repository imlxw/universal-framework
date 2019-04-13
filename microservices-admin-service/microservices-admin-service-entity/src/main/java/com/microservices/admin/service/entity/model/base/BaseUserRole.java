package com.microservices.admin.service.entity.model.base;

import com.microservices.db.model.MicroservicesModel;
import com.jfinal.plugin.activerecord.IBean;

/**
 * Generated by Microservices, do not modify this file.
 */
@SuppressWarnings("serial")
public abstract class BaseUserRole<M extends BaseUserRole<M>> extends MicroservicesModel<M> implements IBean {

	public void setId(java.lang.Long id) {
		set("id", id);
	}

	public java.lang.Long getId() {
		return getLong("id");
	}

	public void setUserId(java.lang.Long userId) {
		set("user_id", userId);
	}

	public java.lang.Long getUserId() {
		return getLong("user_id");
	}

	public void setRoleId(java.lang.Long roleId) {
		set("role_id", roleId);
	}

	public java.lang.Long getRoleId() {
		return getLong("role_id");
	}

}