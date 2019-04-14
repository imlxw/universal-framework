package com.microservices.admin.service.entity.status.system;

import com.microservices.admin.base.common.BaseStatus;

/**
 * 系统资源状态类
 */
public class ResStatus extends BaseStatus {

	public final static String UNUSED = "0";
	public final static String USED = "1";

	public ResStatus() {
		add(UNUSED, "停用");
		add(USED, "启用");
	}

	private static ResStatus me;

	public static ResStatus me() {
		if (me == null) {
			me = new ResStatus();
		}
		return me;
	}

}
