package com.microservices.component.shiro.processer;

import java.util.Arrays;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.AuthorizationException;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresRoles;

public class ShiroRequiresRolesProcesser implements IShiroAuthorizeProcesser {

	private final RequiresRoles requiresRoles;

	public ShiroRequiresRolesProcesser(RequiresRoles requiresRoles) {
		this.requiresRoles = requiresRoles;
	}

	@Override
	public AuthorizeResult authorize() {
		String[] roles = requiresRoles.value();
		try {
			if (roles.length == 1) {
				SecurityUtils.getSubject().checkRole(roles[0]);
				return AuthorizeResult.ok();
			}
			if (Logical.AND.equals(requiresRoles.logical())) {
				SecurityUtils.getSubject().checkRoles(Arrays.asList(roles));
				return AuthorizeResult.ok();
			}
			if (Logical.OR.equals(requiresRoles.logical())) {
				// Avoid processing exceptions unnecessarily - "delay" throwing the exception by calling hasRole first
				boolean hasAtLeastOneRole = false;
				for (String role : roles)
					if (SecurityUtils.getSubject().hasRole(role))
						hasAtLeastOneRole = true;
				// Cause the exception if none of the role match, note that the exception message will be a bit misleading
				if (!hasAtLeastOneRole)
					SecurityUtils.getSubject().checkRole(roles[0]);
			}

			return AuthorizeResult.ok();

		} catch (AuthorizationException e) {
			return AuthorizeResult.fail(AuthorizeResult.ERROR_CODE_UNAUTHORIZATION);
		}

	}
}
