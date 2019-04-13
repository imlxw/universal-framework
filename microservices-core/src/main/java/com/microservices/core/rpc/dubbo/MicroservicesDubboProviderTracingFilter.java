package com.microservices.core.rpc.dubbo;

import com.alibaba.dubbo.common.Constants;
import com.alibaba.dubbo.common.extension.Activate;
import com.alibaba.dubbo.rpc.Filter;
import com.alibaba.dubbo.rpc.Invocation;
import com.alibaba.dubbo.rpc.Invoker;
import com.alibaba.dubbo.rpc.Result;
import com.alibaba.dubbo.rpc.RpcException;
import com.microservices.component.opentracing.MicroservicesOpentracingManager;

import io.opentracing.Span;
import io.opentracing.SpanContext;
import io.opentracing.Tracer;
import io.opentracing.propagation.Format;
import io.opentracing.propagation.TextMapExtractAdapter;

@Activate(group = Constants.PROVIDER)
public class MicroservicesDubboProviderTracingFilter implements Filter {

	@Override
	public Result invoke(Invoker<?> invoker, Invocation inv) throws RpcException {
		Tracer tracer = MicroservicesOpentracingManager.me().getTracer();
		if (tracer == null) {
			return invoker.invoke(inv);
		}

		return processProviderTrace(tracer, invoker, inv);
	}

	protected Result processProviderTrace(Tracer tracer, Invoker<?> invoker, Invocation inv) {
		Span span = extractTraceInfo(tracer, invoker, inv);
		// span.setTag("requestId", request.getRequestId());
		MicroservicesDubboTracingFilterKits.setActiveSpan(span);
		return MicroservicesDubboTracingFilterKits.process(invoker, inv, span);
	}

	protected Span extractTraceInfo(Tracer tracer, Invoker<?> invoker, Invocation inv) {
		String operationName = MicroservicesDubboTracingFilterKits.buildOperationName(invoker, inv);
		Tracer.SpanBuilder span = tracer.buildSpan(operationName);
		try {
			SpanContext spanContext = tracer.extract(Format.Builtin.TEXT_MAP, new TextMapExtractAdapter(inv.getAttachments()));
			if (spanContext != null) {
				span.asChildOf(spanContext);
			}
		} catch (Exception e) {
			span.withTag("Error", "extract from request fail, error msg:" + e.getMessage());
		}
		return span.startManual();
	}

}
