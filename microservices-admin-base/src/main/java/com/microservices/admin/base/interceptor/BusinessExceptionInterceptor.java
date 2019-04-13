package com.microservices.admin.base.interceptor;

import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;
import com.microservices.admin.base.common.RestResult;
import com.microservices.exception.MicroservicesException;
import com.microservices.web.controller.MicroservicesController;

/**
 * 业务异常拦截器
 */
public class BusinessExceptionInterceptor implements Interceptor {

	/** 异常内容在模版引擎中的属性TAG */
	public final static String MESSAGE_TAG = "message";

	/** 异常页面 */
	private String exceptionView = "/exception.html";

	public BusinessExceptionInterceptor(String exceptionView) {
		this.exceptionView = exceptionView;
	}

	@Override
	public void intercept(Invocation inv) {
		try {
			inv.invoke();
		} catch (MicroservicesException e) {
			if (inv.getTarget() instanceof MicroservicesController) {
				MicroservicesController controller = inv.getTarget();

				if (controller.isAjaxRequest()) {
					RestResult<String> restResult = new RestResult<String>();
					restResult.error(e.getMessage());
					controller.renderJson(restResult);
				} else {
					controller.setAttr(MESSAGE_TAG, e.getMessage()).render(exceptionView);
				}
			}
		}
	}

}
