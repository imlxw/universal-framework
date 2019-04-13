package com.microservices.aop.interceptor.metric;

import com.codahale.metrics.Histogram;
import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;
import com.microservices.Microservices;
import com.microservices.component.metric.annotation.EnableMetricHistogram;
import com.microservices.utils.ClassKits;
import com.microservices.utils.StringUtils;

/**
 * 用于在AOP拦截，并通过Metrics的Hsitogram进行统计
 */
public class MicroservicesMetricHistogramAopInterceptor implements Interceptor {

	private static final String suffix = ".histogram";

	@Override
	public void intercept(Invocation inv) {

		EnableMetricHistogram annotation = inv.getMethod().getAnnotation(EnableMetricHistogram.class);

		if (annotation == null) {
			inv.invoke();
			return;
		}

		Class targetClass = ClassKits.getUsefulClass(inv.getTarget().getClass());
		String name = StringUtils.isBlank(annotation.value()) ? targetClass + "." + inv.getMethod().getName() + suffix : annotation.value();

		Histogram histogram = Microservices.me().getMetric().histogram(name);
		histogram.update(annotation.update());
		inv.invoke();
	}
}
