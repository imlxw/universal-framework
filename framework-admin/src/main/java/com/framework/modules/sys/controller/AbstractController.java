package com.framework.modules.sys.controller;

import org.apache.shiro.SecurityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.framework.modules.sys.entity.SysUserEntity;

/**
 * Controller公共组件
 */
public abstract class AbstractController {
	protected Logger logger = LoggerFactory.getLogger(getClass());

	protected SysUserEntity getUser() {
		return (SysUserEntity) SecurityUtils.getSubject().getPrincipal();
	}

	protected Long getUserId() {
		return getUser().getUserId();
	}

	protected Long getDeptId() {
		return getUser().getDeptId();
	}
}
