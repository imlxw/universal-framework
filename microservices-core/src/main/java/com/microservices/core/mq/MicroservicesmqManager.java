package com.microservices.core.mq;

import com.microservices.Microservices;
import com.microservices.core.mq.aliyunmq.MicroservicesAliyunmqImpl;
import com.microservices.core.mq.qpidmq.MicroservicesQpidmqImpl;
import com.microservices.core.mq.rabbitmq.MicroservicesRabbitmqImpl;
import com.microservices.core.mq.redismq.MicroservicesRedismqImpl;
import com.microservices.core.mq.zbus.MicroservicesZbusmqImpl;
import com.microservices.core.spi.MicroservicesSpiLoader;
import com.microservices.utils.ClassKits;

public class MicroservicesmqManager {

	private static MicroservicesmqManager manager;

	public static MicroservicesmqManager me() {
		if (manager == null) {
			manager = ClassKits.singleton(MicroservicesmqManager.class);
		}
		return manager;
	}

	private Microservicesmq microservicesmq;

	public Microservicesmq getMicroservicesmq() {
		if (microservicesmq == null) {
			MicroservicesmqConfig config = Microservices.config(MicroservicesmqConfig.class);
			microservicesmq = getMicroservicesmq(config);
		}
		return microservicesmq;
	}

	public Microservicesmq getMicroservicesmq(MicroservicesmqConfig config) {
		return buildMicroservicesmq(config);
	}

	private Microservicesmq buildMicroservicesmq(MicroservicesmqConfig config) {
		if (config == null) {
			throw new IllegalArgumentException("config must not be null");
		}

		switch (config.getType()) {
			case MicroservicesmqConfig.TYPE_REDIS:
				return new MicroservicesRedismqImpl();
			case MicroservicesmqConfig.TYPE_ALIYUNMQ:
				return new MicroservicesAliyunmqImpl();
			case MicroservicesmqConfig.TYPE_RABBITMQ:
				return new MicroservicesRabbitmqImpl();
			case MicroservicesmqConfig.TYPE_ZBUS:
				return new MicroservicesZbusmqImpl();
			case MicroservicesmqConfig.TYPE_QPID:
				return new MicroservicesQpidmqImpl();
			case MicroservicesmqConfig.TYPE_ACTIVEMQ:
				throw new RuntimeException("not finished!!!!");
			default:
				return MicroservicesSpiLoader.load(Microservicesmq.class, config.getType());
		}

	}
}
