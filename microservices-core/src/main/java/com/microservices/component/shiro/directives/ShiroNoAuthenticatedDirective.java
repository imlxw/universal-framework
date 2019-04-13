package com.microservices.component.shiro.directives;

import com.jfinal.template.Env;
import com.jfinal.template.io.Writer;
import com.jfinal.template.stat.Scope;
import com.microservices.web.directive.annotation.JFinalDirective;

/**
 * 未进行身份验证时，即没有调用Subject.login进行登录。 注意：记住我 自动登录属于未进行身份认证。 #shiroNoAuthenticated() body #end
 */
@JFinalDirective("shiroNoAuthenticated")
public class ShiroNoAuthenticatedDirective extends MicroservicesShiroDirectiveBase {

	@Override
	public void onRender(Env env, Scope scope, Writer writer) {
		if (getSubject() != null && !getSubject().isAuthenticated())
			renderBody(env, scope, writer);
	}

	@Override
	public boolean hasEnd() {
		return true;
	}
}
