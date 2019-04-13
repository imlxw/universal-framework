package com.microservices.component.opentracing;

import com.microservices.utils.StringUtils;
import com.microservices.web.fixedinterceptor.FixedInterceptor;
import com.microservices.web.fixedinterceptor.FixedInvocation;

import io.opentracing.Span;
import io.opentracing.Tracer;

public class OpentracingInterceptor implements FixedInterceptor {

	@Override
	public void intercept(FixedInvocation inv) {

		EnableTracing enableOpentracing = inv.getMethod().getAnnotation(EnableTracing.class);
		Tracer tracer = MicroservicesOpentracingManager.me().getTracer();
		Span span = null;

		if (enableOpentracing != null && tracer != null) {
			String spanName = StringUtils.isBlank(enableOpentracing.value()) ? inv.getController().getClass().getName() + "." + inv.getMethodName() : enableOpentracing.value();

			Tracer.SpanBuilder spanBuilder = tracer.buildSpan(spanName);

			span = spanBuilder.startManual();

			span.setTag("requestId", StringUtils.uuid());
			MicroservicesSpanContext.add(span);
		}

		try {
			inv.invoke();
		} finally {
			if (span != null) {
				span.finish();
				MicroservicesSpanContext.release();
			}
		}

	}

}
