package com.microservices.component.shiro.directives;

import com.jfinal.template.Env;
import com.jfinal.template.expr.ast.ExprList;
import com.jfinal.template.io.Writer;
import com.jfinal.template.stat.Scope;
import com.microservices.utils.ArrayUtils;
import com.microservices.web.directive.annotation.JFinalDirective;

/**
 * 有相应权限 #shiroHasPermission(permissionName) body #end
 */
@JFinalDirective("shiroHasPermission")
public class ShiroHasPermissionDirective extends MicroservicesShiroDirectiveBase {

	@Override
	public void setExprList(ExprList exprList) {
		if (exprList.getExprArray().length != 1) {
			throw new IllegalArgumentException("#shiroHasPermission must has one argument");
		}
		super.setExprList(exprList);
	}

	@Override
	public void onRender(Env env, Scope scope, Writer writer) {
		if (getSubject() != null && ArrayUtils.isNotEmpty(exprList.getExprArray()))
			if (getSubject().isPermitted(exprList.getExprArray()[0].toString())) {
				renderBody(env, scope, writer);
			}

	}

	@Override
	public boolean hasEnd() {
		return true;
	}
}
