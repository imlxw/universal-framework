package com.microservices.component.metric.reporter.jmx;

import com.codahale.metrics.JmxReporter;
import com.codahale.metrics.MetricRegistry;
import com.microservices.component.metric.MicroservicesMetricReporter;

/**
 * @version V1.0
 * @Package com.microservices.component.metric.reporter.jmx
 */
public class JMXReporter implements MicroservicesMetricReporter {
	@Override
	public void report(MetricRegistry metricRegistry) {
		JmxReporter.forRegistry(metricRegistry).build().start();
	}
}
