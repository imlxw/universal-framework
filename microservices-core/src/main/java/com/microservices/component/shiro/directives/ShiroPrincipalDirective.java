package com.microservices.component.shiro.directives;

import com.jfinal.template.Env;
import com.jfinal.template.io.Writer;
import com.jfinal.template.stat.Scope;
import com.microservices.web.directive.annotation.JFinalDirective;

/**
 * 获取Subject Principal 身份信息 #shiroPrincipal() #(principal) #end
 */
@JFinalDirective("shiroPrincipal")
public class ShiroPrincipalDirective extends MicroservicesShiroDirectiveBase {

	@Override
	public void onRender(Env env, Scope scope, Writer writer) {
		if (getSubject() != null && getSubject().getPrincipal() != null) {
			Object principal = getSubject().getPrincipal();
			scope.setLocal("principal", principal);
			renderBody(env, scope, writer);
		}
	}

	@Override
	public boolean hasEnd() {
		return true;
	}
}
