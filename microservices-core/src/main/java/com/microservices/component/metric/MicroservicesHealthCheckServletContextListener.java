package com.microservices.component.metric;

import com.codahale.metrics.health.HealthCheckRegistry;
import com.codahale.metrics.servlets.HealthCheckServlet;

public class MicroservicesHealthCheckServletContextListener extends HealthCheckServlet.ContextListener {

	@Override
	protected HealthCheckRegistry getHealthCheckRegistry() {
		return MicroservicesMetricManager.me().healthCheck();
	}

}