package com.microservices.core.mq.qpidmq;

import com.microservices.config.annotation.PropertyConfig;

@PropertyConfig(prefix = "microservices.mq.qpid")
public class MicroservicesQpidmqConfig {
	private String username = "admin";
	private String password = "admin";

	private String host = "127.0.0.1:5672";
	private String virtualHost;

	private boolean serializerEnable = true;

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

	public String getVirtualHost() {
		return virtualHost;
	}

	public void setVirtualHost(String virtualHost) {
		this.virtualHost = virtualHost;
	}

	public boolean isSerializerEnable() {
		return serializerEnable;
	}

	public void setSerializerEnable(boolean serializerEnable) {
		this.serializerEnable = serializerEnable;
	}
}
