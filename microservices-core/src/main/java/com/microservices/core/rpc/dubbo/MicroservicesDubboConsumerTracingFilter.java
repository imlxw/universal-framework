package com.microservices.core.rpc.dubbo;

import java.util.Iterator;
import java.util.Map;

import com.alibaba.dubbo.common.Constants;
import com.alibaba.dubbo.common.extension.Activate;
import com.alibaba.dubbo.rpc.Filter;
import com.alibaba.dubbo.rpc.Invocation;
import com.alibaba.dubbo.rpc.Invoker;
import com.alibaba.dubbo.rpc.Result;
import com.alibaba.dubbo.rpc.RpcException;
import com.microservices.component.opentracing.MicroservicesOpentracingManager;

import io.opentracing.Span;
import io.opentracing.Tracer;
import io.opentracing.propagation.Format;
import io.opentracing.propagation.TextMap;

@Activate(group = Constants.CONSUMER)
public class MicroservicesDubboConsumerTracingFilter implements Filter {

	@Override
	public Result invoke(Invoker<?> invoker, Invocation inv) throws RpcException {
		Tracer tracer = MicroservicesOpentracingManager.me().getTracer();
		if (tracer == null) {
			return invoker.invoke(inv);
		}

		return processRefererTrace(tracer, invoker, inv);
	}

	protected Result processRefererTrace(Tracer tracer, Invoker<?> invoker, Invocation inv) {
		String operationName = MicroservicesDubboTracingFilterKits.buildOperationName(invoker, inv);
		Tracer.SpanBuilder spanBuilder = tracer.buildSpan(operationName);
		Span activeSpan = MicroservicesDubboTracingFilterKits.getActiveSpan();
		if (activeSpan != null) {
			spanBuilder.asChildOf(activeSpan);
		}
		Span span = spanBuilder.startManual();
		// span.setTag("requestId", request.getRequestId());

		attachTraceInfo(tracer, span, inv);
		return MicroservicesDubboTracingFilterKits.process(invoker, inv, span);

	}

	protected void attachTraceInfo(Tracer tracer, Span span, final Invocation inv) {
		tracer.inject(span.context(), Format.Builtin.TEXT_MAP, new TextMap() {

			@Override
			public void put(String key, String value) {
				inv.getAttachments().put(key, value);
			}

			@Override
			public Iterator<Map.Entry<String, String>> iterator() {
				throw new UnsupportedOperationException("TextMapInjectAdapter should only be used with Tracer.inject()");
			}
		});
	}

}
