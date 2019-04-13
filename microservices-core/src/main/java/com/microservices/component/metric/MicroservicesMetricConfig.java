package com.microservices.component.metric;

import com.microservices.config.annotation.PropertyConfig;

@PropertyConfig(prefix = "microservices.metric")
public class MicroservicesMetricConfig {

	public static final String REPORTER_JMX = "jmx";
	public static final String REPORTER_INFLUXDB = "influxdb";
	public static final String REPORTER_GRAPHITE = "graphite";
	public static final String REPORTER_ELASTICSEARCH = "elasticsearch";
	public static final String REPORTER_GANGLIA = "ganglia";
	public static final String REPORTER_CONSOLE = "console";
	public static final String REPORTER_CSV = "csv";
	public static final String REPORTER_SLF4J = "slf4j";

	private String url;
	private String reporter;

	public String getMappingUrl() {
		// 在metrics中，会访问到配置的二级目录，必须添加下 /* 才能正常访问
		if (url != null && !url.endsWith("/*")) {
			return url + "/*";
		}
		return url;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getReporter() {
		return reporter;
	}

	public void setReporter(String reporter) {
		this.reporter = reporter;
	}
}
