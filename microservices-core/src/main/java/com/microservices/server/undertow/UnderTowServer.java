package com.microservices.server.undertow;

import java.util.Map;
import java.util.Set;

import javax.servlet.DispatcherType;
import javax.servlet.ServletContextListener;

import org.apache.shiro.web.env.EnvironmentLoaderListener;

import com.codahale.metrics.servlets.AdminServlet;
import com.jfinal.core.JFinalFilter;
import com.jfinal.log.Log;
import com.microservices.Microservices;
import com.microservices.component.hystrix.MicroservicesHystrixConfig;
import com.microservices.component.metric.MicroservicesHealthCheckServletContextListener;
import com.microservices.component.metric.MicroservicesMetricConfig;
import com.microservices.component.metric.MicroservicesMetricServletContextListener;
import com.microservices.component.shiro.MicroservicesShiroConfig;
import com.microservices.component.shiro.MicroservicesShiroFilter;
import com.microservices.server.ContextListeners;
import com.microservices.server.MicroservicesServer;
import com.microservices.server.MicroservicesServerClassloader;
import com.microservices.server.MicroservicesServerConfig;
import com.microservices.server.listener.MicroservicesAppListenerManager;
import com.microservices.utils.StringUtils;
import com.microservices.web.MicroservicesWebConfig;
import com.microservices.web.websocket.MicroservicesWebsocketManager;
import com.netflix.hystrix.contrib.metrics.eventstream.HystrixMetricsStreamServlet;

import io.undertow.Handlers;
import io.undertow.Undertow;
import io.undertow.server.DefaultByteBufferPool;
import io.undertow.server.HttpHandler;
import io.undertow.server.handlers.PathHandler;
import io.undertow.server.handlers.resource.ClassPathResourceManager;
import io.undertow.servlet.Servlets;
import io.undertow.servlet.api.DeploymentInfo;
import io.undertow.servlet.api.DeploymentManager;
import io.undertow.servlet.api.ServletContainer;
import io.undertow.servlet.api.ServletInfo;
import io.undertow.websockets.jsr.WebSocketDeploymentInfo;

public class UnderTowServer extends MicroservicesServer {

	static Log log = Log.getLog(UnderTowServer.class);

	private DeploymentManager deploymentManager;
	private DeploymentInfo deploymentInfo;
	private PathHandler pathHandler;
	private Undertow undertow;
	private ServletContainer servletContainer;
	private MicroservicesServerConfig config;
	private MicroservicesWebConfig webConfig;

	public UnderTowServer() {
		config = Microservices.config(MicroservicesServerConfig.class);
		webConfig = Microservices.config(MicroservicesWebConfig.class);

	}

	public void initUndertowServer() {

		MicroservicesServerClassloader classloader = new MicroservicesServerClassloader(UnderTowServer.class.getClassLoader());
		classloader.setDefaultAssertionStatus(false);

		deploymentInfo = buildDeploymentInfo(classloader);

		if (webConfig.isWebsocketEnable()) {
			Set<Class> endPointClasses = MicroservicesWebsocketManager.me().getWebsocketEndPoints();
			WebSocketDeploymentInfo webSocketDeploymentInfo = new WebSocketDeploymentInfo();
			webSocketDeploymentInfo.setBuffers(new DefaultByteBufferPool(true, webConfig.getWebsocketBufferPoolSize()));
			for (Class endPointClass : endPointClasses) {
				webSocketDeploymentInfo.addEndpoint(endPointClass);
			}
			deploymentInfo.addServletContextAttribute(WebSocketDeploymentInfo.ATTRIBUTE_NAME, webSocketDeploymentInfo);
		}

		servletContainer = Servlets.newContainer();
		deploymentManager = servletContainer.addDeployment(deploymentInfo);
		deploymentManager.deploy();

		HttpHandler httpHandler = null;
		try {
			/**
			 * 启动并初始化servlet和filter
			 */
			httpHandler = deploymentManager.start();
		} catch (Throwable ex) {
			ex.printStackTrace();
		}

		pathHandler = Handlers.path(Handlers.resource(new ClassPathResourceManager(classloader, "webRoot")));

		pathHandler.addPrefixPath(config.getContextPath(), httpHandler);

		undertow = Undertow.builder().addHttpListener(config.getPort(), config.getHost()).setHandler(pathHandler).build();

	}

	private DeploymentInfo buildDeploymentInfo(MicroservicesServerClassloader classloader) {
		DeploymentInfo deploymentInfo = Servlets.deployment().setClassLoader(classloader).setResourceManager(new ClassPathResourceManager(classloader)).setContextPath(config.getContextPath())
				.setDeploymentName("microservices" + StringUtils.uuid()).setEagerFilterInit(true); // 设置启动的时候，初始化servlet或filter

		MicroservicesShiroConfig shiroConfig = Microservices.config(MicroservicesShiroConfig.class);
		if (shiroConfig.isConfigOK()) {
			deploymentInfo.addListeners(Servlets.listener(EnvironmentLoaderListener.class));
			deploymentInfo.addFilter(Servlets.filter("shiro", MicroservicesShiroFilter.class)).addFilterUrlMapping("shiro", shiroConfig.getUrlMapping(), DispatcherType.REQUEST);
		}

		deploymentInfo.addFilter(Servlets.filter("jfinal", JFinalFilter.class).addInitParam("configClass", Microservices.me().getMicroservicesConfig().getJfinalConfig())).addFilterUrlMapping("jfinal", "/*", DispatcherType.REQUEST);

		MicroservicesHystrixConfig hystrixConfig = Microservices.config(MicroservicesHystrixConfig.class);
		if (StringUtils.isNotBlank(hystrixConfig.getUrl())) {
			deploymentInfo.addServlets(Servlets.servlet("HystrixMetricsStreamServlet", HystrixMetricsStreamServlet.class).addMapping(hystrixConfig.getUrl()));
		}

		MicroservicesMetricConfig metricsConfig = Microservices.config(MicroservicesMetricConfig.class);
		if (StringUtils.isNotBlank(metricsConfig.getMappingUrl())) {
			deploymentInfo.addServlets(Servlets.servlet("MetricsAdminServlet", AdminServlet.class).addMapping(metricsConfig.getMappingUrl()));

			deploymentInfo.addListeners(Servlets.listener(MicroservicesMetricServletContextListener.class));
			deploymentInfo.addListeners(Servlets.listener(MicroservicesHealthCheckServletContextListener.class));
		}

		com.microservices.server.Servlets microservicesServlets = new com.microservices.server.Servlets();
		ContextListeners listeners = new ContextListeners();

		MicroservicesAppListenerManager.me().onMicroservicesDeploy(microservicesServlets, listeners);

		for (Map.Entry<String, com.microservices.server.Servlets.ServletInfo> entry : microservicesServlets.getServlets().entrySet()) {
			ServletInfo servletInfo = Servlets.servlet(entry.getKey(), entry.getValue().getServletClass()).addMappings(entry.getValue().getUrlMapping());
			deploymentInfo.addServlet(servletInfo);
		}

		for (Class<? extends ServletContextListener> listenerClass : listeners.getListeners()) {
			deploymentInfo.addListeners(Servlets.listener(listenerClass));
		}

		deploymentInfo.addServlets(Servlets.servlet("MicroservicesResourceServlet", MicroservicesResourceServlet.class).addMapping("/*"));

		return deploymentInfo;
	}

	@Override
	public boolean start() {
		try {
			initUndertowServer();
			MicroservicesAppListenerManager.me().onAppStartBefore(this);
			undertow.start();
		} catch (Throwable ex) {
			log.error("can not start undertow with port:" + config.getPort(), ex);
			stop();
			return false;
		}
		return true;
	}

	@Override
	public boolean restart() {
		try {
			stop();
			start();
			System.err.println("undertow restarted!!!");
		} catch (Throwable ex) {
			return false;
		}

		return true;
	}

	@Override
	public boolean stop() {

		deploymentManager.undeploy();
		servletContainer.removeDeployment(deploymentInfo);

		if (pathHandler != null) {
			pathHandler.clearPaths();
		}
		if (undertow != null) {
			undertow.stop();
		}

		return true;
	}

	public DeploymentManager getDeploymentManager() {
		return deploymentManager;
	}

	public DeploymentInfo getDeploymentInfo() {
		return deploymentInfo;
	}

	public PathHandler getPathHandler() {
		return pathHandler;
	}

	public Undertow getUndertow() {
		return undertow;
	}

	public ServletContainer getServletContainer() {
		return servletContainer;
	}

	public MicroservicesServerConfig getConfig() {
		return config;
	}
}
