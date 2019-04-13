package com.microservices.component.metric;

import java.util.ArrayList;
import java.util.List;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.health.HealthCheckRegistry;
import com.jfinal.log.Log;
import com.microservices.Microservices;
import com.microservices.component.metric.reporter.console.MicroservicesConsoleReporter;
import com.microservices.component.metric.reporter.csv.CSVReporter;
import com.microservices.component.metric.reporter.elasticsearch.ElasticsearchReporter;
import com.microservices.component.metric.reporter.ganglia.GangliaReporter;
import com.microservices.component.metric.reporter.graphite.MicroservicesGraphiteReporter;
import com.microservices.component.metric.reporter.influxdb.InfluxdbReporter;
import com.microservices.component.metric.reporter.jmx.JMXReporter;
import com.microservices.component.metric.reporter.slf4j.MicroservicesSlf4jReporter;
import com.microservices.core.spi.MicroservicesSpiLoader;
import com.microservices.utils.ArrayUtils;
import com.microservices.utils.StringUtils;

public class MicroservicesMetricManager {

	private static final Log LOG = Log.getLog(MicroservicesMetricManager.class);

	private static MicroservicesMetricManager me;

	public static MicroservicesMetricManager me() {
		if (me == null) {
			me = new MicroservicesMetricManager();
		}
		return me;
	}

	private MetricRegistry metricRegistry;
	private HealthCheckRegistry healthCheckRegistry;
	private MicroservicesMetricConfig metricsConfig = Microservices.config(MicroservicesMetricConfig.class);

	private MicroservicesMetricManager() {
		metricRegistry = new MetricRegistry();
		healthCheckRegistry = new HealthCheckRegistry();

		List<MicroservicesMetricReporter> reporters = getReporters();
		if (ArrayUtils.isNullOrEmpty(reporters)) {
			LOG.warn("metrics reporter is empty . please config \"microservices.metric.reporter = xxx\" in microservices.properties ");
			return;
		}

		for (MicroservicesMetricReporter reporter : reporters) {
			try {
				reporter.report(metricRegistry);
			} catch (Throwable ex) {
				LOG.error(ex.toString(), ex);
			}
		}
	}

	private List<MicroservicesMetricReporter> getReporters() {
		String repoterString = metricsConfig.getReporter();
		if (StringUtils.isBlank(repoterString)) {
			return null;
		}
		List<MicroservicesMetricReporter> reporters = new ArrayList<>();

		String[] repoterStrings = repoterString.split(";");
		for (String repoterName : repoterStrings) {
			MicroservicesMetricReporter reporter = getReporterByName(repoterName);
			if (reporter != null) {
				reporters.add(reporter);
			}
		}

		return reporters;
	}

	private MicroservicesMetricReporter getReporterByName(String repoterName) {

		MicroservicesMetricReporter reporter = null;

		switch (repoterName) {
			case MicroservicesMetricConfig.REPORTER_JMX:
				reporter = new JMXReporter();
				break;
			case MicroservicesMetricConfig.REPORTER_INFLUXDB:
				reporter = new InfluxdbReporter();
				break;
			case MicroservicesMetricConfig.REPORTER_GRAPHITE:
				reporter = new MicroservicesGraphiteReporter();
				break;
			case MicroservicesMetricConfig.REPORTER_ELASTICSEARCH:
				reporter = new ElasticsearchReporter();
				break;
			case MicroservicesMetricConfig.REPORTER_GANGLIA:
				reporter = new GangliaReporter();
				break;
			case MicroservicesMetricConfig.REPORTER_CONSOLE:
				reporter = new MicroservicesConsoleReporter();
				break;
			case MicroservicesMetricConfig.REPORTER_CSV:
				reporter = new CSVReporter();
				break;
			case MicroservicesMetricConfig.REPORTER_SLF4J:
				reporter = new MicroservicesSlf4jReporter();
				break;
			default:
				reporter = MicroservicesSpiLoader.load(MicroservicesMetricReporter.class, repoterName);
		}

		return reporter;
	}

	public MetricRegistry metric() {
		return metricRegistry;
	}

	public HealthCheckRegistry healthCheck() {
		return healthCheckRegistry;
	}

}
