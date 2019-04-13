package com.microservices.core.http;

import com.microservices.config.annotation.PropertyConfig;

@PropertyConfig(prefix = "microservices.http")
public class MicroservicesHttpConfig {
	public static final String TYPE_DEFAULT = "default";
	public static final String TYPE_HTTPCLIENT = "httpclient";
	public static final String TYPE_OKHTTP = "okhttp";

	public String type = TYPE_DEFAULT;

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
}
