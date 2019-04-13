package com.microservices.component.opentracing;

import com.microservices.Microservices;

import brave.Tracing;
import brave.opentracing.BraveTracer;
import io.opentracing.Tracer;
import zipkin2.Span;
import zipkin2.reporter.AsyncReporter;
import zipkin2.reporter.urlconnection.URLConnectionSender;

public class ZipkinTracerFactory implements TracerFactory {

	final Tracer tracer;

	public ZipkinTracerFactory() {

		MicroservicesOpentracingConfig config = Microservices.config(MicroservicesOpentracingConfig.class);

		URLConnectionSender sender = URLConnectionSender.newBuilder().endpoint(config.getUrl()).connectTimeout(config.getConnectTimeout()).compressionEnabled(config.isCompressionEnabled()).readTimeout(config.getReadTimeout()).build();

		AsyncReporter<Span> reporter = AsyncReporter.builder(sender).build();

		Tracing tracing = Tracing.newBuilder().spanReporter(reporter).localServiceName(config.getServiceName()).build();

		tracer = BraveTracer.newBuilder(tracing).build();
	}

	@Override
	public Tracer getTracer() {
		return tracer;
	}
}
