package com.microservices.admin.base.interceptor;

import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;
import com.jfinal.core.Controller;
import com.microservices.admin.base.common.RestResult;
import com.microservices.utils.ArrayUtils;
import com.microservices.utils.StringUtils;
import com.microservices.web.controller.MicroservicesController;

import java.lang.reflect.Method;

/**
 * 非空参数拦截器
 */
public class NotNullParaInterceptor implements Interceptor {

	/** 异常页面 */
	private String exceptionView = "/exception.html";

	public NotNullParaInterceptor(String exceptionView) {
		this.exceptionView = exceptionView;
	}

	@Override
	public void intercept(Invocation inv) {
		Method method = inv.getMethod();

		NotNullPara notNullPara = method.getAnnotation(NotNullPara.class);
		if (notNullPara == null) {
			inv.invoke();
			return;
		}

		String[] paraKeys = notNullPara.value();
		if (ArrayUtils.isNullOrEmpty(paraKeys)) {
			inv.invoke();
			return;
		}

		for (String param : paraKeys) {
			String value = inv.getController().getPara(param);
			if (value == null || value.trim().length() == 0) {
				renderError(inv, param, notNullPara.errorRedirect());
				return;
			}
		}

		inv.invoke();
	}

	private void renderError(Invocation inv, String param, String errorRedirect) {
		if (StringUtils.isNotBlank(errorRedirect)) {
			inv.getController().redirect(errorRedirect);
			return;
		}

		Controller controller = inv.getController();
		if (controller instanceof MicroservicesController) {
			MicroservicesController jc = (MicroservicesController) controller;
			if (jc.isAjaxRequest()) {
				jc.renderJson(RestResult.buildError("参数[" + param + "]不可为空"));
				return;
			}
		}
		controller.setAttr(BusinessExceptionInterceptor.MESSAGE_TAG, "参数[" + param + "]不可为空").render(exceptionView);
	}
}
