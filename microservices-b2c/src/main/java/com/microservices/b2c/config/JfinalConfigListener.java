package com.microservices.b2c.config;

import com.google.inject.Binder;
import com.jfinal.captcha.CaptchaManager;
import com.jfinal.config.Constants;
import com.jfinal.config.Interceptors;
import com.jfinal.config.Routes;
import com.jfinal.ext.handler.ContextPathHandler;
import com.jfinal.template.Engine;
import com.microservices.Microservices;
import com.microservices.admin.base.captcha.CaptchaCache;
import com.microservices.admin.base.common.AppInfo;
import com.microservices.admin.base.interceptor.BusinessExceptionInterceptor;
import com.microservices.admin.base.interceptor.NotNullParaInterceptor;
import com.microservices.admin.base.web.render.AppRenderFactory;
import com.microservices.aop.jfinal.JfinalHandlers;
import com.microservices.aop.jfinal.JfinalPlugins;
import com.microservices.server.ContextListeners;
import com.microservices.server.MicroservicesServer;
import com.microservices.server.Servlets;
import com.microservices.server.listener.MicroservicesAppListenerBase;

/**
 * jfinal config
 */
public class JfinalConfigListener extends MicroservicesAppListenerBase {
	@Override
	public void onJfinalConstantConfig(Constants constants) {
		constants.setError401View("/template/401.html");
		constants.setError403View("/template/403.html");
		constants.setError404View("/template/404.html");
		constants.setError500View("/template/500.html");
		constants.setRenderFactory(new AppRenderFactory());
	}

	@Override
	public void onJfinalRouteConfig(Routes routes) {
		routes.setBaseViewPath("/template");
	}

	@Override
	public void onJfinalEngineConfig(Engine engine) {
		engine.setDevMode(true);
		AppInfo app = Microservices.config(AppInfo.class);
		engine.addSharedObject("APP", app);
		engine.addSharedObject("RESOURCE_HOST", app.getResourceHost());
	}

	@Override
	public void onInterceptorConfig(Interceptors interceptors) {
		interceptors.add(new NotNullParaInterceptor("/template/exception.html"));
		interceptors.add(new BusinessExceptionInterceptor("/template/exception.html"));
	}

	@Override
	public void onJfinalPluginConfig(JfinalPlugins plugins) {

	}

	@Override
	public void onHandlerConfig(JfinalHandlers handlers) {
		handlers.add(new ContextPathHandler("ctxPath"));
	}

	@Override
	public void onJFinalStarted() {

	}

	@Override
	public void onJFinalStop() {
	}

	@Override
	public void onMicroservicesStarted() {
		/** 集群模式下验证码使用 redis 缓存 */
		CaptchaManager.me().setCaptchaCache(new CaptchaCache());
	}

	@Override
	public void onAppStartBefore(MicroservicesServer microservicesServer) {

	}

	@Override
	public void onMicroservicesDeploy(Servlets servlets, ContextListeners listeners) {

	}

	@Override
	public void onGuiceConfigure(Binder binder) {

	}
}
