package com.microservices.core.rpc.zbus;

import com.microservices.config.annotation.PropertyConfig;

@PropertyConfig(prefix = "microservices.rpc.zbus")
public class MicroservicesZbusRpcConfig {

	private String serviceName;
	private String serviceToken;

	public String getServiceName() {
		return serviceName;
	}

	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}

	public String getServiceToken() {
		return serviceToken;
	}

	public void setServiceToken(String serviceToken) {
		this.serviceToken = serviceToken;
	}
}
