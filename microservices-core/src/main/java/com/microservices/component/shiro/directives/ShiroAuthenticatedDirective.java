package com.microservices.component.shiro.directives;

import com.jfinal.template.Env;
import com.jfinal.template.io.Writer;
import com.jfinal.template.stat.Scope;
import com.microservices.web.directive.annotation.JFinalDirective;

/**
 * 用户已经身份验证通过，Subject.login登录成功 #shiroAuthenticated() body #end
 */
@JFinalDirective("shiroAuthenticated")
public class ShiroAuthenticatedDirective extends MicroservicesShiroDirectiveBase {

	@Override
	public void onRender(Env env, Scope scope, Writer writer) {

		if (getSubject() != null && getSubject().isAuthenticated()) {
			renderBody(env, scope, writer);
		}

	}

	@Override
	public boolean hasEnd() {
		return true;
	}
}
