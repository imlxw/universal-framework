package com.microservices.aop.interceptor.metric;

import com.codahale.metrics.Counter;
import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;
import com.microservices.Microservices;
import com.microservices.component.metric.annotation.EnableMetricCounter;
import com.microservices.utils.ClassKits;
import com.microservices.utils.StringUtils;

/**
 * 用于在AOP拦截，并通过Metrics的Conter进行统计
 */
public class MicroservicesMetricCounterAopInterceptor implements Interceptor {

	private static final String suffix = ".counter";

	@Override
	public void intercept(Invocation inv) {

		EnableMetricCounter annotation = inv.getMethod().getAnnotation(EnableMetricCounter.class);

		if (annotation == null) {
			inv.invoke();
			return;
		}

		Class targetClass = ClassKits.getUsefulClass(inv.getTarget().getClass());
		String name = StringUtils.isBlank(annotation.value()) ? targetClass.getName() + "." + inv.getMethod().getName() + suffix : annotation.value();

		Counter counter = Microservices.me().getMetric().counter(name);
		counter.inc();
		inv.invoke();
	}
}
