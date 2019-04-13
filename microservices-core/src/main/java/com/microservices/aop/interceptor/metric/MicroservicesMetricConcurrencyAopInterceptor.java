package com.microservices.aop.interceptor.metric;

import com.codahale.metrics.Counter;
import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;
import com.microservices.Microservices;
import com.microservices.component.metric.annotation.EnableMetricConcurrency;
import com.microservices.utils.ClassKits;
import com.microservices.utils.StringUtils;

/**
 * 用于在AOP拦截，并通过Metrics的Conter进行统计
 */
public class MicroservicesMetricConcurrencyAopInterceptor implements Interceptor {

	private static final String suffix = ".concurrency";

	@Override
	public void intercept(Invocation inv) {

		EnableMetricConcurrency annotation = inv.getMethod().getAnnotation(EnableMetricConcurrency.class);

		if (annotation == null) {
			inv.invoke();
			return;
		}

		Class targetClass = ClassKits.getUsefulClass(inv.getTarget().getClass());
		String name = StringUtils.isBlank(annotation.value()) ? targetClass + "." + inv.getMethod().getName() + suffix : annotation.value();

		Counter counter = Microservices.me().getMetric().counter(name);
		try {
			counter.inc();
			inv.invoke();
		} finally {
			counter.dec();
		}
	}
}
