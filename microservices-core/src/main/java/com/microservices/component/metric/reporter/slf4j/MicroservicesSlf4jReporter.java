package com.microservices.component.metric.reporter.slf4j;

import java.util.concurrent.TimeUnit;

import org.slf4j.LoggerFactory;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Slf4jReporter;
import com.microservices.component.metric.MicroservicesMetricReporter;

/**
 * @version V1.0
 * @Package com.microservices.component.metric.reporter.slf4j
 */
public class MicroservicesSlf4jReporter implements MicroservicesMetricReporter {
	@Override
	public void report(MetricRegistry metricRegistry) {
		final Slf4jReporter reporter = Slf4jReporter.forRegistry(metricRegistry).outputTo(LoggerFactory.getLogger(MicroservicesSlf4jReporter.class)).convertRatesTo(TimeUnit.SECONDS).convertDurationsTo(TimeUnit.MILLISECONDS).build();
		reporter.start(1, TimeUnit.MINUTES);
	}
}
