package com.microservices.component.shiro.directives;

import com.jfinal.template.Env;
import com.jfinal.template.io.Writer;
import com.jfinal.template.stat.Scope;
import com.microservices.web.directive.annotation.JFinalDirective;

/**
 * 游客访问时。 但是，当用户登录成功了就不显示了 #shiroGuest() body #end
 */
@JFinalDirective("shiroGuest")
public class ShiroGuestDirective extends MicroservicesShiroDirectiveBase {

	@Override
	public void onRender(Env env, Scope scope, Writer writer) {

		if (getSubject() == null || getSubject().getPrincipal() == null) {
			renderBody(env, scope, writer);
		}

	}

	@Override
	public boolean hasEnd() {
		return true;
	}
}
