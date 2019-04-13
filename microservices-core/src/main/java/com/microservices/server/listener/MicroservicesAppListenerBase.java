package com.microservices.server.listener;

import com.google.inject.Binder;
import com.jfinal.config.Constants;
import com.jfinal.config.Interceptors;
import com.jfinal.config.Routes;
import com.jfinal.template.Engine;
import com.microservices.aop.jfinal.JfinalHandlers;
import com.microservices.aop.jfinal.JfinalPlugins;
import com.microservices.server.ContextListeners;
import com.microservices.server.MicroservicesServer;
import com.microservices.server.Servlets;
import com.microservices.web.fixedinterceptor.FixedInterceptors;

public class MicroservicesAppListenerBase implements MicroservicesAppListener {

	@Override
	public void onMicroservicesDeploy(Servlets servlets, ContextListeners listeners) {

	}

	@Override
	public void onJfinalConstantConfig(Constants constants) {

	}

	@Override
	public void onJfinalRouteConfig(Routes routes) {

	}

	@Override
	public void onJfinalEngineConfig(Engine engine) {

	}

	@Override
	public void onJfinalPluginConfig(JfinalPlugins plugins) {

	}

	@Override
	public void onInterceptorConfig(Interceptors interceptors) {

	}

	@Override
	public void onFixedInterceptorConfig(FixedInterceptors fixedInterceptors) {

	}

	@Override
	public void onHandlerConfig(JfinalHandlers handlers) {

	}

	@Override
	public void onJFinalStarted() {

	}

	@Override
	public void onJFinalStop() {

	}

	@Override
	public void onMicroservicesStarted() {

	}

	@Override
	public void onAppStartBefore(MicroservicesServer underTowServer) {

	}

	@Override
	public void onGuiceConfigure(Binder binder) {

	}
}
