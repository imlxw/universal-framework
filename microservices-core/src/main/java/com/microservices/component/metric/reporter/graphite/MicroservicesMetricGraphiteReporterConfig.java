package com.microservices.component.metric.reporter.graphite;

import com.microservices.config.annotation.PropertyConfig;

@PropertyConfig(prefix = "microservices.metric.reporter.graphite")
public class MicroservicesMetricGraphiteReporterConfig {

	private String host;
	private Integer port = 2003;

	private String prefixedWith;

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public Integer getPort() {
		return port;
	}

	public void setPort(Integer port) {
		this.port = port;
	}

	public String getPrefixedWith() {
		return prefixedWith;
	}

	public void setPrefixedWith(String prefixedWith) {
		this.prefixedWith = prefixedWith;
	}
}
