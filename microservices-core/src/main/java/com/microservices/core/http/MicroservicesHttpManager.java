package com.microservices.core.http;

import com.microservices.Microservices;
import com.microservices.core.http.microservices.MicroservicesHttpImpl;
import com.microservices.core.http.okhttp.OKHttpImpl;
import com.microservices.core.spi.MicroservicesSpiLoader;
import com.microservices.utils.ClassKits;

public class MicroservicesHttpManager {

	private static MicroservicesHttpManager manager;

	public static MicroservicesHttpManager me() {
		if (manager == null) {
			manager = ClassKits.singleton(MicroservicesHttpManager.class);
		}
		return manager;
	}

	private MicroservicesHttp microservicesHttp;

	public MicroservicesHttp getMicroservicesHttp() {
		if (microservicesHttp == null) {
			microservicesHttp = buildMicroservicesHttp();
		}
		return microservicesHttp;
	}

	private MicroservicesHttp buildMicroservicesHttp() {
		MicroservicesHttpConfig config = Microservices.config(MicroservicesHttpConfig.class);

		switch (config.getType()) {
			case MicroservicesHttpConfig.TYPE_DEFAULT:
				return new MicroservicesHttpImpl();
			case MicroservicesHttpConfig.TYPE_OKHTTP:
				return new OKHttpImpl();
			case MicroservicesHttpConfig.TYPE_HTTPCLIENT:
				throw new RuntimeException("not finished!!!!");
			default:
				return MicroservicesSpiLoader.load(MicroservicesHttp.class, config.getType());
		}

	}

}
