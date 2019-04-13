package com.microservices.core.rpc.zbus;

import io.zbus.rpc.bootstrap.mq.ServiceBootstrap;

/**
 * @version V1.0
 * @Package com.microservices.core.rpc.zbus
 */
public class MicroservicesServiceBootstrap extends ServiceBootstrap {

	public ServiceBootstrap addModule(Class clazz, Object impl, String group, String version) {
		String module = ZbusKits.buildModule(clazz, group, version);
		this.processor.addModule(module, impl);
		return this;
	}

}
