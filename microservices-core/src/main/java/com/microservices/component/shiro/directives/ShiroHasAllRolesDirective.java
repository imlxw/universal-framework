package com.microservices.component.shiro.directives;

import java.util.ArrayList;
import java.util.List;

import com.jfinal.template.Env;
import com.jfinal.template.expr.ast.Expr;
import com.jfinal.template.expr.ast.ExprList;
import com.jfinal.template.io.Writer;
import com.jfinal.template.stat.Scope;
import com.microservices.utils.ArrayUtils;
import com.microservices.web.directive.annotation.JFinalDirective;

/**
 * 拥有全部角色 #shiroHasAllRoles(roleName1,roleName2) body #end
 */
@JFinalDirective("shiroHasAllRoles")
public class ShiroHasAllRolesDirective extends MicroservicesShiroDirectiveBase {

	@Override
	public void setExprList(ExprList exprList) {
		if (exprList.getExprArray().length == 0) {
			throw new IllegalArgumentException("#shiroHasAllRoles argument must not be empty");
		}
		super.setExprList(exprList);
	}

	@Override
	public void onRender(Env env, Scope scope, Writer writer) {
		if (getSubject() != null && ArrayUtils.isNotEmpty(exprList.getExprArray())) {
			List<String> roles = new ArrayList<String>();
			for (Expr expr : exprList.getExprArray())
				roles.add(expr.toString());

			if (getSubject().hasAllRoles(roles))
				renderBody(env, scope, writer);

		}
	}

	@Override
	public boolean hasEnd() {
		return true;
	}

}