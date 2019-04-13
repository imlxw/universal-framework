package com.microservices.web.handler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.jfinal.handler.Handler;
import com.microservices.Microservices;
import com.microservices.MicroservicesConstants;
import com.microservices.component.hystrix.MicroservicesHystrixConfig;
import com.microservices.component.metric.MicroservicesMetricConfig;
import com.microservices.exception.MicroservicesExceptionHolder;
import com.microservices.web.MicroservicesRequestContext;
import com.microservices.web.session.MicroservicesServletRequestWrapper;
import com.microservices.web.websocket.MicroservicesWebsocketManager;
import com.netflix.hystrix.strategy.concurrency.HystrixRequestContext;

public class MicroservicesHandler extends Handler {

	private static MicroservicesMetricConfig metricsConfig = Microservices.config(MicroservicesMetricConfig.class);
	private static MicroservicesHystrixConfig hystrixConfig = Microservices.config(MicroservicesHystrixConfig.class);

	@Override
	public void handle(String target, HttpServletRequest request, HttpServletResponse response, boolean[] isHandled) {

		if (target.indexOf('.') != -1 // static files
				|| MicroservicesWebsocketManager.me().isWebsokcetEndPoint(target) // websocket
				|| (metricsConfig.getUrl() != null && target.startsWith(metricsConfig.getUrl())) // metrics
				|| (hystrixConfig.getUrl() != null && target.startsWith(hystrixConfig.getUrl()))) // hystrix
		{
			return;
		}

		/**
		 * 通过 MicroservicesRequestContext 去保存 request，然后可以在当前线程的任何地方 通过 MicroservicesRequestContext.getRequest() 去获取。
		 */
		MicroservicesServletRequestWrapper microservicesServletRequest = new MicroservicesServletRequestWrapper(request, response);
		MicroservicesRequestContext.handle(microservicesServletRequest, response);

		/**
		 * 初始化 当前线程的 Hystrix
		 */
		HystrixRequestContext context = HystrixRequestContext.initializeContext();

		/**
		 * 初始化 异常记录器，用于记录异常信息，然后在页面输出
		 */
		MicroservicesExceptionHolder.init();

		try {
			/**
			 * 执行请求逻辑
			 */
			doHandle(target, microservicesServletRequest, response, isHandled);

		} finally {
			MicroservicesExceptionHolder.release();
			context.shutdown();
			MicroservicesRequestContext.release();

			microservicesServletRequest.refreshSession();
		}

	}

	private void doHandle(String target, HttpServletRequest request, HttpServletResponse response, boolean[] isHandled) {
		request.setAttribute(MicroservicesConstants.ATTR_REQUEST, request);
		request.setAttribute(MicroservicesConstants.ATTR_CONTEXT_PATH, request.getContextPath());
		next.handle(target, request, response, isHandled);
	}

}
