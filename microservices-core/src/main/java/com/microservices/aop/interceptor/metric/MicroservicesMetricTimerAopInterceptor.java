package com.microservices.aop.interceptor.metric;

import com.codahale.metrics.Timer;
import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;
import com.microservices.Microservices;
import com.microservices.component.metric.annotation.EnableMetricTimer;
import com.microservices.utils.ClassKits;
import com.microservices.utils.StringUtils;

/**
 * 用于在AOP拦截，并通过Metrics的Timer进行统计
 */
public class MicroservicesMetricTimerAopInterceptor implements Interceptor {

	private static final String suffix = ".timer";

	@Override
	public void intercept(Invocation inv) {

		EnableMetricTimer annotation = inv.getMethod().getAnnotation(EnableMetricTimer.class);

		if (annotation == null) {
			inv.invoke();
			return;
		}

		Class targetClass = ClassKits.getUsefulClass(inv.getTarget().getClass());
		String name = StringUtils.isBlank(annotation.value()) ? targetClass + "." + inv.getMethod().getName() + suffix : annotation.value();

		Timer meter = Microservices.me().getMetric().timer(name);
		Timer.Context timerContext = meter.time();
		try {
			inv.invoke();
		} finally {
			timerContext.stop();
		}
	}
}
