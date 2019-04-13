package com.microservices.component.shiro.directives;

import com.jfinal.template.Env;
import com.jfinal.template.expr.ast.Expr;
import com.jfinal.template.expr.ast.ExprList;
import com.jfinal.template.io.Writer;
import com.jfinal.template.stat.Scope;
import com.microservices.utils.ArrayUtils;
import com.microservices.web.directive.annotation.JFinalDirective;

/**
 * 拥有全部权限 #shiroHasAllPermission(permissionName1,permissionName2) body #end
 */
@JFinalDirective("shiroHasAllPermission")
public class ShiroHasAllPermissionDirective extends MicroservicesShiroDirectiveBase {

	@Override
	public void setExprList(ExprList exprList) {
		if (exprList.getExprArray().length == 0) {
			throw new IllegalArgumentException("#shiroHasAllPermission argument must not be empty");
		}
		super.setExprList(exprList);
	}

	@Override
	public void onRender(Env env, Scope scope, Writer writer) {
		if (getSubject() != null && ArrayUtils.isNotEmpty(exprList.getExprArray())) {
			boolean hasAllPermission = true;
			for (Expr expr : exprList.getExprArray())
				if (!getSubject().isPermitted(expr.toString())) {
					hasAllPermission = false;
					break;
				}

			if (hasAllPermission) {
				renderBody(env, scope, writer);
			}

		}
	}

	@Override
	public boolean hasEnd() {
		return true;
	}
}
