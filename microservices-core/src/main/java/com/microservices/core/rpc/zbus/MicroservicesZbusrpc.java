package com.microservices.core.rpc.zbus;

import com.microservices.Microservices;
import com.microservices.core.rpc.MicroservicesrpcBase;
import com.microservices.utils.StringUtils;

public class MicroservicesZbusrpc extends MicroservicesrpcBase {

	MicroservicesServiceBootstrap serviceBootstrap;
	MicroservicesClientBootstrap clientBootstrap;

	MicroservicesZbusRpcConfig zbusConfig = Microservices.config(MicroservicesZbusRpcConfig.class);

	public MicroservicesZbusrpc() {
		if (StringUtils.isBlank(zbusConfig.getServiceName())) {
			throw new NullPointerException("please config microservices.rpc.zbus.serviceName in your properties.");
		}

		serviceBootstrap = new MicroservicesServiceBootstrap();
		clientBootstrap = new MicroservicesClientBootstrap();
		clientBootstrap.serviceAddress(getRpcConfig().getRegistryAddress());
		clientBootstrap.serviceName(zbusConfig.getServiceName());
		if (StringUtils.isNotBlank(zbusConfig.getServiceToken())) {
			clientBootstrap.serviceToken(zbusConfig.getServiceToken());
		}

	}

	@Override
	public <T> T serviceObtain(Class<T> serviceClass, String group, String version) {
		if (StringUtils.isBlank(group)) {
			group = getRpcConfig().getDefaultGroup();
		}
		return clientBootstrap.serviceObtain(serviceClass, group, version);
	}

	@Override
	public <T> boolean serviceExport(Class<T> interfaceClass, Object object, String group, String version, int port) {
		serviceBootstrap.addModule(interfaceClass, object, group, version);
		return true;
	}

	@Override
	public void onInited() {
		try {
			serviceBootstrap.serviceAddress(getRpcConfig().getRegistryAddress());
			serviceBootstrap.serviceName(zbusConfig.getServiceName());
			if (StringUtils.isNotBlank(zbusConfig.getServiceToken())) {
				serviceBootstrap.serviceToken(zbusConfig.getServiceToken());
			}
			serviceBootstrap.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
