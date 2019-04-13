package com.microservices.component.opentracing;

import io.opentracing.Tracer;

public interface TracerFactory {

	public Tracer getTracer();

}
