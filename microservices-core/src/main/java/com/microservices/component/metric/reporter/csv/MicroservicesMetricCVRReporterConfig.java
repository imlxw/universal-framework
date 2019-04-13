package com.microservices.component.metric.reporter.csv;

import com.microservices.config.annotation.PropertyConfig;

@PropertyConfig(prefix = "microservices.metric.reporter.cvr")
public class MicroservicesMetricCVRReporterConfig {

	private String path;

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}
}
