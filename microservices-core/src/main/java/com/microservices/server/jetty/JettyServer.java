package com.microservices.server.jetty;

import java.io.File;
import java.net.InetSocketAddress;
import java.net.URISyntaxException;
import java.util.EnumSet;
import java.util.Map;

import javax.servlet.DispatcherType;
import javax.servlet.ServletContextListener;

import org.apache.shiro.web.env.EnvironmentLoaderListener;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.FilterHolder;
import org.eclipse.jetty.servlet.ServletContextHandler;

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
import com.microservices.utils.ClassKits;
import com.microservices.utils.StringUtils;
import com.microservices.web.MicroservicesWebConfig;
import com.netflix.hystrix.contrib.metrics.eventstream.HystrixMetricsStreamServlet;

public class JettyServer extends MicroservicesServer {

	private static Log log = Log.getLog(JettyServer.class);

	private MicroservicesServerConfig config;
	private MicroservicesWebConfig webConfig;

	private Server jettyServer;
	private ServletContextHandler handler;

	public JettyServer() {
		config = Microservices.config(MicroservicesServerConfig.class);
		webConfig = Microservices.config(MicroservicesWebConfig.class);
	}

	@Override
	public boolean start() {
		try {
			initJettyServer();
			MicroservicesAppListenerManager.me().onAppStartBefore(this);
			jettyServer.start();
		} catch (Throwable ex) {
			log.error(ex.toString(), ex);
			stop();
			return false;
		}
		return true;
	}

	private void initJettyServer() {
		InetSocketAddress address = new InetSocketAddress(config.getHost(), config.getPort());
		jettyServer = new Server(address);

		handler = new ServletContextHandler();
		handler.setContextPath(config.getContextPath());
		handler.setClassLoader(new MicroservicesServerClassloader(JettyServer.class.getClassLoader()));
		handler.setResourceBase(getRootClassPath());

		MicroservicesShiroConfig shiroConfig = Microservices.config(MicroservicesShiroConfig.class);
		if (shiroConfig.isConfigOK()) {
			handler.addEventListener(new EnvironmentLoaderListener());
			handler.addFilter(MicroservicesShiroFilter.class, shiroConfig.getUrlMapping(), EnumSet.of(DispatcherType.REQUEST));
		}

		// JFinal
		FilterHolder jfinalFilter = handler.addFilter(JFinalFilter.class, "/*", EnumSet.of(DispatcherType.REQUEST));
		jfinalFilter.setInitParameter("configClass", Microservices.me().getMicroservicesConfig().getJfinalConfig());

		MicroservicesHystrixConfig hystrixConfig = Microservices.config(MicroservicesHystrixConfig.class);
		if (StringUtils.isNotBlank(hystrixConfig.getUrl())) {
			handler.addServlet(HystrixMetricsStreamServlet.class, hystrixConfig.getUrl());
		}

		MicroservicesMetricConfig metricsConfig = Microservices.config(MicroservicesMetricConfig.class);
		if (StringUtils.isNotBlank(metricsConfig.getMappingUrl())) {
			handler.addEventListener(new MicroservicesMetricServletContextListener());
			handler.addEventListener(new MicroservicesHealthCheckServletContextListener());
			handler.addServlet(AdminServlet.class, metricsConfig.getMappingUrl());
		}

		com.microservices.server.Servlets microservicesServlets = new com.microservices.server.Servlets();
		ContextListeners listeners = new ContextListeners();

		MicroservicesAppListenerManager.me().onMicroservicesDeploy(microservicesServlets, listeners);

		for (Map.Entry<String, com.microservices.server.Servlets.ServletInfo> entry : microservicesServlets.getServlets().entrySet()) {
			for (String path : entry.getValue().getUrlMapping()) {
				handler.addServlet(entry.getValue().getServletClass(), path);
			}
		}

		for (Class<? extends ServletContextListener> listenerClass : listeners.getListeners()) {
			handler.addEventListener(ClassKits.newInstance(listenerClass));
		}

		jettyServer.setHandler(handler);
	}

	private static String getRootClassPath() {
		String path = null;
		try {
			path = JettyServer.class.getClassLoader().getResource("").toURI().getPath();
			return new File(path).getAbsolutePath();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		return path;
	}

	@Override
	public boolean restart() {
		stop();
		start();
		return true;
	}

	@Override
	public boolean stop() {
		try {
			jettyServer.stop();
			return true;
		} catch (Exception ex) {
			log.error("can not start jetty with port:" + config.getPort(), ex);
		}
		return false;
	}

	public Server getJettyServer() {
		return jettyServer;
	}

	public ServletContextHandler getHandler() {
		return handler;
	}

}
