package com.microservices.web.directive.base;

import com.jfinal.template.Directive;
import com.jfinal.template.Env;
import com.jfinal.template.expr.ast.ExprList;
import com.jfinal.template.io.Writer;
import com.jfinal.template.stat.Scope;
import com.microservices.Microservices;

/**
 * Jfinal 指令的基类
 */
public abstract class MicroservicesDirectiveBase extends Directive {

	public MicroservicesDirectiveBase() {
		Microservices.injectMembers(this);
	}

	@Override
	public void setExprList(ExprList exprList) {
		super.setExprList(exprList);
	}

	@Override
	public void exec(Env env, Scope scope, Writer writer) {
		scope = new Scope(scope);
		scope.getCtrl().setLocalAssignment();
		exprList.eval(scope);

		onRender(env, scope, writer);

	}

	public abstract void onRender(Env env, Scope scope, Writer writer);

	public <T> T getParam(String key, T defaultValue, Scope scope) {
		Object data = scope.getLocal(key);
		return (T) (data == null ? defaultValue : data);
	}

	public <T> T getParam(String key, Scope scope) {
		return getParam(key, null, scope);
	}

	public <T> T getParam(int index, T defaultValue, Scope scope) {
		Object data = exprList.getExpr(index).eval(scope);
		return (T) (data == null ? defaultValue : data);
	}

	public <T> T getParam(int index, Scope scope) {
		return getParam(index, null, scope);
	}

	public void renderBody(Env env, Scope scope, Writer writer) {
		stat.exec(env, scope, writer);
	}

}
