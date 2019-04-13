package com.microservices.component.shiro.directives;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;

import com.microservices.web.directive.base.MicroservicesDirectiveBase;

/**
 * Shiro 指令的基类
 */
public abstract class MicroservicesShiroDirectiveBase extends MicroservicesDirectiveBase {
	/**
	 * @return Subject 用户信息
	 */
	protected Subject getSubject() {
		return SecurityUtils.getSubject();
	}

}
