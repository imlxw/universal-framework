package com.microservices.component.opentracing;

import io.opentracing.Span;

/**
 * openTracing span 的上下文
 */
public class MicroservicesSpanContext {
	private static ThreadLocal<Span> spans = new ThreadLocal<>();

	public static void add(Span span) {
		spans.set(span);
	}

	public static Span get() {
		return spans.get();
	}

	public static void release() {
		spans.remove();
	}
}
