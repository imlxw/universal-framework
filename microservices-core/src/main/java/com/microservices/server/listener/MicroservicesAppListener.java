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

public interface MicroservicesAppListener {

	public void onMicroservicesDeploy(Servlets servlets, ContextListeners listeners);

	public void onJfinalConstantConfig(Constants constants);

	public void onJfinalRouteConfig(Routes routes);

	public void onJfinalEngineConfig(Engine engine);

	public void onJfinalPluginConfig(JfinalPlugins plugins);

	public void onInterceptorConfig(Interceptors interceptors);

	public void onFixedInterceptorConfig(FixedInterceptors fixedInterceptors);

	public void onHandlerConfig(JfinalHandlers handlers);

	public void onJFinalStarted();

	public void onJFinalStop();

	public void onMicroservicesStarted();

	public void onAppStartBefore(MicroservicesServer microservicesServer);

	public void onGuiceConfigure(Binder binder);
}
