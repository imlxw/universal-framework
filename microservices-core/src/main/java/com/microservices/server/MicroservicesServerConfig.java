package com.microservices.server;

import com.microservices.config.annotation.PropertyConfig;

@PropertyConfig(prefix = "microservices.server")
public class MicroservicesServerConfig {

	public static final String TYPE_UNDERTOW = "undertow";
	public static final String TYPE_TOMCAT = "tomcat";
	public static final String TYPE_JETTY = "jetty";

	private String type = TYPE_UNDERTOW;
	private String host = "0.0.0.0";
	private int port = 8080;
	private String contextPath = "/";

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public String getContextPath() {
		return contextPath;
	}

	public void setContextPath(String contextPath) {
		this.contextPath = contextPath;
	}

	@Override
	public String toString() {
		return "MicroservicesServerConfig {" + "type='" + type + '\'' + ", host='" + host + '\'' + ", port=" + port + ", contextPath='" + contextPath + '\'' + '}';
	}
}
