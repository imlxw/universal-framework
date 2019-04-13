package com.microservices.component.opentracing;

import org.apache.skywalking.apm.toolkit.opentracing.SkywalkingTracer;

import io.opentracing.Tracer;

/**
 * Skywalking 手动埋点支撑
 */
public class SkywalkingTracerFactory implements TracerFactory {

	private Tracer tracer;

	public SkywalkingTracerFactory() {
		tracer = new SkywalkingTracer();
	}

	@Override
	public Tracer getTracer() {
		return tracer;
	}
}
