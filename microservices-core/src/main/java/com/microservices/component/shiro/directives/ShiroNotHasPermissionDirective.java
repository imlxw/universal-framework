package com.microservices.component.shiro.directives;

import com.jfinal.template.Env;
import com.jfinal.template.expr.ast.ExprList;
import com.jfinal.template.io.Writer;
import com.jfinal.template.stat.Scope;
import com.microservices.utils.ArrayUtils;
import com.microservices.web.directive.annotation.JFinalDirective;

/**
 * 没有该权限 #shiroNotHasPermission(permissionName) body #end
 */
@JFinalDirective("shiroNotHasPermission")
public class ShiroNotHasPermissionDirective extends MicroservicesShiroDirectiveBase {

	@Override
	public void setExprList(ExprList exprList) {
		if (exprList.getExprArray().length != 1) {
			throw new IllegalArgumentException("#shiroNotHasPermission must has one argument");
		}
		super.setExprList(exprList);
	}

	@Override
	public void onRender(Env env, Scope scope, Writer writer) {
		if (getSubject() != null && ArrayUtils.isNotEmpty(exprList.getExprArray()))
			if (!getSubject().isPermitted(exprList.getExprArray()[0].toString()))
				renderBody(env, scope, writer);
	}

	@Override
	public boolean hasEnd() {
		return true;
	}
}
