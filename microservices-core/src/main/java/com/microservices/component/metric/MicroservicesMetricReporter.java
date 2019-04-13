package com.microservices.component.metric;

import com.codahale.metrics.MetricRegistry;

/**
 * @version V1.0
 * @Package com.microservices.component.metrics
 */
public interface MicroservicesMetricReporter {

	public void report(MetricRegistry metricRegistry);
}
