package com.microservices.core.rpc.dubbo;

import com.alibaba.dubbo.common.Constants;
import com.alibaba.dubbo.rpc.Invocation;
import com.alibaba.dubbo.rpc.Invoker;
import com.alibaba.dubbo.rpc.Result;
import com.alibaba.dubbo.rpc.RpcContext;
import com.jfinal.log.Log;
import com.microservices.component.opentracing.MicroservicesSpanContext;

import io.opentracing.Span;

public class MicroservicesDubboTracingFilterKits {

	static Log log = Log.getLog(MicroservicesDubboTracingFilterKits.class);

	public static Result process(Invoker<?> invoker, Invocation inv, Span span) {
		Throwable ex = null;
		boolean exception = true;
		try {
			Result response = invoker.invoke(inv);
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

	public static String buildOperationName(Invoker<?> invoker, Invocation inv) {
		String version = invoker.getUrl().getParameter(Constants.VERSION_KEY);
		String group = invoker.getUrl().getParameter(Constants.GROUP_KEY);

		StringBuilder sn = new StringBuilder("Dubbo_");
		sn.append(group).append(":").append(version);
		sn.append("_");
		sn.append(invoker.getInterface().getName()).append(".");

		sn.append(inv.getMethodName());
		sn.append("(");
		Class<?>[] types = inv.getParameterTypes();
		if (types != null && types.length > 0) {
			boolean first = true;
			for (Class<?> type : types) {
				if (first) {
					first = false;
				} else {
					sn.append(",");
				}
				sn.append(type.getName());
			}
		}
		sn.append(") ");

		return sn.toString();
	}

	public static final String ACTIVE_SPAN = "ot_active_span";

	public static Span getActiveSpan() {
		Object span = RpcContext.getContext().get(ACTIVE_SPAN);
		if (span != null && span instanceof Span) {
			return (Span) span;
		}

		/**
		 * 当通过 RpcContext 去获取不到的时候，有可能此线程 由于 hystrix 的原因，或其他原因，已经处于和RpcContext不同的线程 所以通过 RpcContext 去获取不到当前的Span信息 在程序中，当启动新的线程进行操作的时候，会通过 MicroservicesSpanContext.add(span) 来设置新线程的span内容
		 */
		return MicroservicesSpanContext.get();
	}

	public static void setActiveSpan(Span span) {
		RpcContext.getContext().set(ACTIVE_SPAN, span);
	}

}
