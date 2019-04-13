package com.microservices.aop.interceptor.metric;

import com.codahale.metrics.Meter;
import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;
import com.microservices.Microservices;
import com.microservices.component.metric.annotation.EnableMetricMeter;
import com.microservices.utils.ClassKits;
import com.microservices.utils.StringUtils;

/**
 * 用于在AOP拦截，并通过Metrics的Meter进行统计
 */
public class MicroservicesMetricMeterAopInterceptor implements Interceptor {

	private static final String suffix = ".meter";

	@Override
	public void intercept(Invocation inv) {

		EnableMetricMeter annotation = inv.getMethod().getAnnotation(EnableMetricMeter.class);

		if (annotation == null) {
			inv.invoke();
			return;
		}

		Class targetClass = ClassKits.getUsefulClass(inv.getTarget().getClass());
		String name = StringUtils.isBlank(annotation.value()) ? targetClass + "." + inv.getMethod().getName() + suffix : annotation.value();

		Meter meter = Microservices.me().getMetric().meter(name);
		meter.mark();
		inv.invoke();
	}
}
