package com.microservices.component.opentracing;

import com.microservices.Microservices;
import com.microservices.core.spi.MicroservicesSpiLoader;

import io.opentracing.Tracer;

public class MicroservicesOpentracingManager {

	private static final MicroservicesOpentracingManager me = new MicroservicesOpentracingManager();

	public static MicroservicesOpentracingManager me() {
		return me;
	}

	private TracerFactory tracerFactory;

	public MicroservicesOpentracingManager() {
		MicroservicesOpentracingConfig config = Microservices.config(MicroservicesOpentracingConfig.class);

		if (!config.isConfigOk()) {
			return;
		}

		if (MicroservicesOpentracingConfig.TYPE_ZIPKIN.equals(config.getType())) {
			tracerFactory = new ZipkinTracerFactory();
		} else {
			tracerFactory = MicroservicesSpiLoader.load(TracerFactory.class, config.getType());
		}

	}

	public Tracer getTracer() {
		return tracerFactory != null ? tracerFactory.getTracer() : null;
	}

}
