package com.microservices.admin.validator.system;

import com.jfinal.core.Controller;
import com.microservices.admin.base.web.base.JsonValidator;

/**
 * 系统资源校验器
 */
public class ResValidator extends JsonValidator {

	@Override
	protected void validate(Controller c) {
		String methodName = getActionMethod().getName();
		if ("postAdd".equals(methodName)) {
			validateRequiredString("pid", "父资源编码为空");
			validateRequiredString("res.name", "菜单名称为空");
			validateRequiredString("res.url", "菜单URL为空");
			validateRequiredString("res.des", "菜单描述为空");
			validateRequiredString("res.type", "菜单类型为空");
			validateRequiredString("res.seq", "排序号为空");
			validateRequiredString("res.status", "资源状态为空");
		} else if ("postUpdate".equals(methodName)) {
			validateRequiredString("pid", "父资源编码为空");
			validateRequiredString("res.name", "菜单名称为空");
			validateRequiredString("res.url", "菜单URL为空");
			validateRequiredString("res.des", "菜单描述为空");
			validateRequiredString("res.type", "菜单类型为空");
			validateRequiredString("res.seq", "排序号为空");
			validateRequiredString("res.status", "资源状态为空");
		}
	}
}
