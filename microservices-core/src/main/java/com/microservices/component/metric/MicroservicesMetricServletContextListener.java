package com.microservices.component.metric;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.servlets.MetricsServlet;

public class MicroservicesMetricServletContextListener extends MetricsServlet.ContextListener {

	@Override
	protected MetricRegistry getMetricRegistry() {
		return MicroservicesMetricManager.me().metric();
	}

}