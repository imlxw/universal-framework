package com.microservices.component.metric;

import com.codahale.metrics.Counter;
import com.codahale.metrics.Histogram;
import com.codahale.metrics.Meter;
import com.codahale.metrics.Timer;
import com.microservices.Microservices;
import com.microservices.component.metric.annotation.EnableMetricConcurrency;
import com.microservices.component.metric.annotation.EnableMetricCounter;
import com.microservices.component.metric.annotation.EnableMetricHistogram;
import com.microservices.component.metric.annotation.EnableMetricMeter;
import com.microservices.component.metric.annotation.EnableMetricTimer;
import com.microservices.utils.StringUtils;
import com.microservices.web.fixedinterceptor.FixedInterceptor;
import com.microservices.web.fixedinterceptor.FixedInvocation;

/**
 * 用于对controller的Metrics 统计 注意：如果 Controller通过 @Clear 来把此 拦截器给清空，那么此方法（action）注入将会失效
 */
public class MicroservicesMetricInterceptor implements FixedInterceptor {

	@Override
	public void intercept(FixedInvocation inv) {

		Timer.Context timerContext = null;

		EnableMetricCounter counterAnnotation = inv.getMethod().getAnnotation(EnableMetricCounter.class);
		if (counterAnnotation != null) {
			String name = StringUtils.isBlank(counterAnnotation.value()) ? inv.getController().getClass().getName() + "." + inv.getMethodName() + ".counter" : counterAnnotation.value();

			Counter counter = Microservices.me().getMetric().counter(name);
			counter.inc();
		}

		Counter concurrencyRecord = null;
		EnableMetricConcurrency concurrencyAnnotation = inv.getMethod().getAnnotation(EnableMetricConcurrency.class);
		if (concurrencyAnnotation != null) {
			String name = StringUtils.isBlank(concurrencyAnnotation.value()) ? inv.getController().getClass().getName() + "." + inv.getMethodName() + ".concurrency" : concurrencyAnnotation.value();

			concurrencyRecord = Microservices.me().getMetric().counter(name);
			concurrencyRecord.inc();
		}

		EnableMetricMeter meterAnnotation = inv.getMethod().getAnnotation(EnableMetricMeter.class);
		if (meterAnnotation != null) {
			String name = StringUtils.isBlank(meterAnnotation.value()) ? inv.getController().getClass().getName() + "." + inv.getMethodName() + ".meter" : meterAnnotation.value();

			Meter meter = Microservices.me().getMetric().meter(name);
			meter.mark();
		}

		EnableMetricHistogram histogramAnnotation = inv.getMethod().getAnnotation(EnableMetricHistogram.class);
		if (histogramAnnotation != null) {
			String name = StringUtils.isBlank(histogramAnnotation.value()) ? inv.getController().getClass().getName() + "." + inv.getMethodName() + ".histogram" : histogramAnnotation.value();

			Histogram histogram = Microservices.me().getMetric().histogram(name);
			histogram.update(histogramAnnotation.update());
		}

		EnableMetricTimer timerAnnotation = inv.getMethod().getAnnotation(EnableMetricTimer.class);
		if (timerAnnotation != null) {
			String name = StringUtils.isBlank(timerAnnotation.value()) ? inv.getController().getClass().getName() + "." + inv.getMethodName() + ".timer" : timerAnnotation.value();

			Timer timer = Microservices.me().getMetric().timer(name);
			timerContext = timer.time();
		}

		try {
			inv.invoke();
		} finally {
			if (concurrencyRecord != null) {
				concurrencyRecord.dec();
			}
			if (timerContext != null) {
				timerContext.stop();
			}
		}

	}

}
