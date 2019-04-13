package com.microservices.component.shiro.processer;

import org.apache.shiro.SecurityUtils;

public class ShiroRequiresGuestProcesser implements IShiroAuthorizeProcesser {

	@Override
	public AuthorizeResult authorize() {
		return SecurityUtils.getSubject().getPrincipal() != null ? AuthorizeResult.fail(AuthorizeResult.ERROR_CODE_UNAUTHENTICATED) : AuthorizeResult.ok();
	}
}
