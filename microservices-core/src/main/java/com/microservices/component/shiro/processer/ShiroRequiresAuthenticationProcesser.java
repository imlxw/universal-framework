package com.microservices.component.shiro.processer;

import org.apache.shiro.SecurityUtils;

public class ShiroRequiresAuthenticationProcesser implements IShiroAuthorizeProcesser {

	@Override
	public AuthorizeResult authorize() {
		return !SecurityUtils.getSubject().isAuthenticated() ? AuthorizeResult.fail(AuthorizeResult.ERROR_CODE_UNAUTHENTICATED) : AuthorizeResult.ok();
	}
}
