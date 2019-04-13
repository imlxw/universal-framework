package com.microservices.component.metric.reporter.graphite;

import java.net.InetSocketAddress;
import java.util.concurrent.TimeUnit;

import com.codahale.metrics.MetricFilter;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.graphite.Graphite;
import com.codahale.metrics.graphite.GraphiteReporter;
import com.microservices.Microservices;
import com.microservices.component.metric.MicroservicesMetricReporter;
import com.microservices.utils.StringUtils;

/**
 * @version V1.0
 * @Package com.microservices.component.metric.reporter.graphite
 */
public class MicroservicesGraphiteReporter implements MicroservicesMetricReporter {
	@Override
	public void report(MetricRegistry metricRegistry) {

		MicroservicesMetricGraphiteReporterConfig config = Microservices.config(MicroservicesMetricGraphiteReporterConfig.class);

		if (StringUtils.isBlank(config.getHost())) {
			throw new NullPointerException("graphite reporter host must not be null, please config microservices.metrics.reporter.graphite.host in you properties.");
		}
		if (config.getPort() == null) {
			throw new NullPointerException("graphite reporter port must not be null, please config microservices.metrics.reporter.graphite.port in you properties.");
		}
		if (config.getPrefixedWith() == null) {
			throw new NullPointerException("graphite reporter prefixedWith must not be null, please config microservices.metrics.reporter.graphite.prefixedWith in you properties.");
		}

		Graphite graphite = new Graphite(new InetSocketAddress(config.getHost(), config.getPort()));

		GraphiteReporter reporter = GraphiteReporter.forRegistry(metricRegistry).prefixedWith(config.getPrefixedWith()).convertRatesTo(TimeUnit.SECONDS).convertDurationsTo(TimeUnit.MILLISECONDS).filter(MetricFilter.ALL).build(graphite);

		reporter.start(1, TimeUnit.MINUTES);
	}
}
