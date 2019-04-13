package com.microservices.component.opentracing;

import com.microservices.config.annotation.PropertyConfig;

@PropertyConfig(prefix = "microservices.tracing")
public class MicroservicesOpentracingConfig {

	public static final String TYPE_ZIPKIN = "zipkin";
	public static final String TYPE_SKYWALKING = "skyWalking";

	private String type = TYPE_ZIPKIN;
	private String serviceName;
	private String url;
	private int connectTimeout = 1000 * 10;
	private int readTimeout = 1000 * 60;
	private boolean compressionEnabled = true;

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getServiceName() {
		return serviceName;
	}

	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public int getConnectTimeout() {
		return connectTimeout;
	}

	public void setConnectTimeout(int connectTimeout) {
		this.connectTimeout = connectTimeout;
	}

	public int getReadTimeout() {
		return readTimeout;
	}

	public void setReadTimeout(int readTimeout) {
		this.readTimeout = readTimeout;
	}

	public boolean isCompressionEnabled() {
		return compressionEnabled;
	}

	public void setCompressionEnabled(boolean compressionEnabled) {
		this.compressionEnabled = compressionEnabled;
	}

	public boolean isConfigOk() {
		return url != null && serviceName != null;
	}
}
