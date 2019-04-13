package com.microservices.component.metric.reporter.console;

import java.util.concurrent.TimeUnit;

import com.codahale.metrics.ConsoleReporter;
import com.codahale.metrics.MetricRegistry;
import com.microservices.component.metric.MicroservicesMetricReporter;

/**
 * @version V1.0
 * @Package com.microservices.component.metric.reporter.console
 */
public class MicroservicesConsoleReporter implements MicroservicesMetricReporter {
	@Override
	public void report(MetricRegistry metricRegistry) {
		final ConsoleReporter reporter = ConsoleReporter.forRegistry(metricRegistry).convertRatesTo(TimeUnit.SECONDS).convertDurationsTo(TimeUnit.MILLISECONDS).build();
		reporter.start(1, TimeUnit.MINUTES);
	}
}
