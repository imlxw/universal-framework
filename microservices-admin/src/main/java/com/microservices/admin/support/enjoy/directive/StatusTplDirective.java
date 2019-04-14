package com.microservices.admin.support.enjoy.directive;

import com.jfinal.template.Env;
import com.jfinal.template.io.Writer;
import com.jfinal.template.stat.ParseException;
import com.jfinal.template.stat.Scope;
import com.microservices.admin.base.common.BaseStatus;
import com.microservices.web.directive.annotation.JFinalDirective;
import com.microservices.web.directive.base.MicroservicesDirectiveBase;

/**
 * 状态layui模版，状态数据desc
 */
@JFinalDirective("statusTpl")
public class StatusTplDirective extends MicroservicesDirectiveBase {

	/** 状态类全路径 例如：com.AbcStatus */
	private BaseStatus status;
	/** 属性名默认status，laytpl 里 d.attrName */
	private String attrName;

	@Override
	public void exec(Env env, Scope scope, Writer writer) {
		if (exprList.length() > 2) {
			throw new ParseException("Wrong number parameter of #date directive, two parameters allowed at most", location);
		}

		status = getParam(0, scope);
		if (status == null) {
			throw new ParseException("status is null", location);
		}

		if (exprList.length() > 1) {
			attrName = getParam(1, "status", scope);
		}

		write(writer, "<div>");
		for (String key : status.all().keySet()) {
			write(writer, "{{#  if(d." + attrName + " == \\'" + key + "\\') { }}");
			write(writer, status.desc(key));
			write(writer, "{{#  } }}");
		}
		write(writer, "</div>");
	}

	@Override
	public void onRender(Env env, Scope scope, Writer writer) {

	}
}
