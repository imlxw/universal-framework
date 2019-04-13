package com.microservices.core.rpc.motan;

import java.util.Iterator;
import java.util.Map;

import com.jfinal.log.Log;
import com.microservices.component.opentracing.MicroservicesOpentracingManager;
import com.microservices.component.opentracing.MicroservicesSpanContext;
import com.weibo.api.motan.core.extension.Activation;
import com.weibo.api.motan.core.extension.SpiMeta;
import com.weibo.api.motan.filter.Filter;
import com.weibo.api.motan.rpc.Caller;
import com.weibo.api.motan.rpc.Provider;
import com.weibo.api.motan.rpc.Request;
import com.weibo.api.motan.rpc.Response;
import com.weibo.api.motan.rpc.RpcContext;
import com.weibo.api.motan.util.MotanFrameworkUtil;

import io.opentracing.Span;
import io.opentracing.SpanContext;
import io.opentracing.Tracer;
import io.opentracing.propagation.Format;
import io.opentracing.propagation.TextMap;
import io.opentracing.propagation.TextMapExtractAdapter;

@SpiMeta(name = "microservicesOpentracing")
@Activation(sequence = 30)
public class MicroservicesMotanTracingFilter implements Filter {

	static Log log = Log.getLog(MicroservicesMotanTracingFilter.class);

	@Override
	public Response filter(Caller<?> caller, Request request) {

		Tracer tracer = MicroservicesOpentracingManager.me().getTracer();

		if (tracer == null) {
			return caller.call(request);
		}

		// 服务器
		if (caller instanceof Provider) {
			return processProviderTrace(tracer, caller, request);
		}

		// 客户端
		else {
			return processRefererTrace(tracer, caller, request);
		}
	}

	/**
	 * process trace in client end
	 *
	 * @param caller
	 * @param request
	 * @return
	 */
	protected Response processRefererTrace(Tracer tracer, Caller<?> caller, Request request) {
		String operationName = buildOperationName(request);
		Tracer.SpanBuilder spanBuilder = tracer.buildSpan(operationName);
		Span activeSpan = getActiveSpan();
		if (activeSpan != null) {
			spanBuilder.asChildOf(activeSpan);
		}
		Span span = spanBuilder.startManual();
		span.setTag("requestId", request.getRequestId());

		attachTraceInfo(tracer, span, request);
		return process(caller, request, span);

	}

	protected Response process(Caller<?> caller, Request request, Span span) {
		Exception ex = null;
		boolean exception = true;
		try {
			Response response = caller.call(request);
			if (response.getException() != null) {
				ex = response.getException();
			} else {
				exception = false;
			}
			return response;
		} catch (RuntimeException e) {
			ex = e;
			throw e;
		} finally {
			try {
				if (exception) {
					span.log("request fail." + (ex == null ? "unknown exception" : ex.getMessage()));
				} else {
					span.log("request success.");
				}
				span.finish();
			} catch (Exception e) {
				log.error("opentracing span finish error!", e);
			}
		}
	}

	protected String buildOperationName(Request request) {
		return "Motan_" + MotanFrameworkUtil.getGroupMethodString(request);
	}

	protected void attachTraceInfo(Tracer tracer, Span span, final Request request) {
		tracer.inject(span.context(), Format.Builtin.TEXT_MAP, new TextMap() {

			@Override
			public void put(String key, String value) {
				request.setAttachment(key, value);
			}

			@Override
			public Iterator<Map.Entry<String, String>> iterator() {
				throw new UnsupportedOperationException("TextMapInjectAdapter should only be used with Tracer.inject()");
			}
		});
	}

	/**
	 * process trace in server end
	 *
	 * @param caller
	 * @param request
	 * @return
	 */
	protected Response processProviderTrace(Tracer tracer, Caller<?> caller, Request request) {
		Span span = extractTraceInfo(request, tracer);
		span.setTag("requestId", request.getRequestId());
		setActiveSpan(span);
		return process(caller, request, span);
	}

	protected Span extractTraceInfo(Request request, Tracer tracer) {
		String operationName = buildOperationName(request);
		Tracer.SpanBuilder span = tracer.buildSpan(operationName);
		try {
			SpanContext spanContext = tracer.extract(Format.Builtin.TEXT_MAP, new TextMapExtractAdapter(request.getAttachments()));
			if (spanContext != null) {
				span.asChildOf(spanContext);
			}
		} catch (Exception e) {
			span.withTag("Error", "extract from request fail, error msg:" + e.getMessage());
		}
		return span.startManual();
	}

	// replace TracerFactory with any tracer implementation
	public static final String ACTIVE_SPAN = "ot_active_span";

	public static Span getActiveSpan() {
		Object span = RpcContext.getContext().getAttribute(ACTIVE_SPAN);
		if (span != null && span instanceof Span) {
			return (Span) span;
		}

		/**
		 * 当通过 RpcContext 去获取不到的时候，有可能此线程 由于 hystrix 的原因，或其他原因，已经处于和RpcContext不同的线程 所以通过 RpcContext 去获取不到当前的Span信息 在程序中，当启动新的线程进行操作的时候，会通过 MicroservicesOpentracingManager.me().initSpan(span) 来设置新线程的span内容
		 */
		return MicroservicesSpanContext.get();
	}

	public static void setActiveSpan(Span span) {
		RpcContext.getContext().putAttribute(ACTIVE_SPAN, span);
	}
}
