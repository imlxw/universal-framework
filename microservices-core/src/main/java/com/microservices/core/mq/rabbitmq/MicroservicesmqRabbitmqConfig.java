package com.microservices.core.mq.rabbitmq;

import com.microservices.config.annotation.PropertyConfig;

@PropertyConfig(prefix = "microservices.mq.rabbitmq")
public class MicroservicesmqRabbitmqConfig {

	private String username = "guest";
	private String password = "guest";

	private String host = "127.0.0.1";
	private String port = "5672";
	private String virtualHost;

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public String getPort() {
		return port;
	}

	public int getPortAsInt() {
		return Integer.valueOf(port);
	}

	public void setPort(String port) {
		this.port = port;
	}

	public String getVirtualHost() {
		return virtualHost;
	}

	public void setVirtualHost(String virtualHost) {
		this.virtualHost = virtualHost;
	}

}
