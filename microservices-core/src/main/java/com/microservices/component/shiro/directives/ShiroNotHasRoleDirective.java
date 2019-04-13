package com.microservices.component.shiro.directives;

import com.jfinal.template.Env;
import com.jfinal.template.expr.ast.Expr;
import com.jfinal.template.expr.ast.ExprList;
import com.jfinal.template.io.Writer;
import com.jfinal.template.stat.Scope;
import com.microservices.utils.ArrayUtils;
import com.microservices.web.directive.annotation.JFinalDirective;

/**
 * 没有该角色 #shiroNotHasRole(roleName) body #end
 */
@JFinalDirective("shiroNotHasRole")
public class ShiroNotHasRoleDirective extends MicroservicesShiroDirectiveBase {

	@Override
	public void setExprList(ExprList exprList) {
		if (exprList.getExprArray().length == 0) {
			throw new IllegalArgumentException("#shiroNotHasRole argument must not be empty");
		}
		super.setExprList(exprList);
	}

	@Override
	public void onRender(Env env, Scope scope, Writer writer) {
		boolean hasAnyRole = false;
		if (getSubject() != null && ArrayUtils.isNotEmpty(exprList.getExprArray())) {
			for (Expr expr : exprList.getExprArray()) {
				if (getSubject().hasRole(expr.toString())) {
					hasAnyRole = true;
					break;
				}
			}
		}
		if (!hasAnyRole)
			renderBody(env, scope, writer);
	}

	@Override
	public boolean hasEnd() {
		return true;
	}

}