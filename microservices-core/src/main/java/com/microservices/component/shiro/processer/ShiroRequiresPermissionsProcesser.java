package com.microservices.component.shiro.processer;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.AuthorizationException;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.subject.Subject;

public class ShiroRequiresPermissionsProcesser implements IShiroAuthorizeProcesser {

	private final RequiresPermissions requiresPermissions;

	public ShiroRequiresPermissionsProcesser(RequiresPermissions requiresPermissions) {
		this.requiresPermissions = requiresPermissions;
	}

	@Override
	public AuthorizeResult authorize() {
		try {
			String[] perms = requiresPermissions.value();
			Subject subject = SecurityUtils.getSubject();

			if (perms.length == 1) {
				subject.checkPermission(perms[0]);
				return AuthorizeResult.ok();
			}
			if (Logical.AND.equals(requiresPermissions.logical())) {
				subject.checkPermissions(perms);
				return AuthorizeResult.ok();
			}
			if (Logical.OR.equals(requiresPermissions.logical())) {
				// Avoid processing exceptions unnecessarily - "delay" throwing the
				// exception by calling hasRole first
				boolean hasAtLeastOnePermission = false;
				for (String permission : perms)
					if (subject.isPermitted(permission))
						hasAtLeastOnePermission = true;
				// Cause the exception if none of the role match, note that the
				// exception message will be a bit misleading
				if (!hasAtLeastOnePermission)
					subject.checkPermission(perms[0]);

			}

			return AuthorizeResult.ok();

		} catch (AuthorizationException e) {
			return AuthorizeResult.fail(AuthorizeResult.ERROR_CODE_UNAUTHORIZATION);
		}
	}
}
