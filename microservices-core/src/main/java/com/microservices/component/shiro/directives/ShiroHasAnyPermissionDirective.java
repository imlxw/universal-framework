package com.microservices.component.shiro.directives;

import com.jfinal.template.Env;
import com.jfinal.template.expr.ast.Expr;
import com.jfinal.template.expr.ast.ExprList;
import com.jfinal.template.io.Writer;
import com.jfinal.template.stat.Scope;
import com.microservices.utils.ArrayUtils;
import com.microservices.web.directive.annotation.JFinalDirective;

/**
 * 拥有任何一个权限 #shiroHasAnyPermission(permission1,permission2) body #end
 */
@JFinalDirective("shiroHasAnyPermission")
public class ShiroHasAnyPermissionDirective extends MicroservicesShiroDirectiveBase {

	@Override
	public void setExprList(ExprList exprList) {
		if (exprList.getExprArray().length == 0) {
			throw new IllegalArgumentException("#shiroHasAnyPermission argument must not be empty");
		}
		super.setExprList(exprList);
	}

	@Override
	public void onRender(Env env, Scope scope, Writer writer) {
		if (getSubject() != null && ArrayUtils.isNotEmpty(exprList.getExprArray())) {
			for (Expr expr : exprList.getExprArray()) {
				if (getSubject().isPermitted(expr.toString())) {
					renderBody(env, scope, writer);
					break;
				}
			}
		}
	}

	@Override
	public boolean hasEnd() {
		return true;
	}

}