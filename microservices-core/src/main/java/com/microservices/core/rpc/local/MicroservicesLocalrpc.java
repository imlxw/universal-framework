package com.microservices.core.rpc.local;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.microservices.core.rpc.MicroservicesrpcBase;

public class MicroservicesLocalrpc extends MicroservicesrpcBase {

	Map<Class, Object> objectMap = new ConcurrentHashMap<>();

	@Override
	public <T> T serviceObtain(Class<T> serviceClass, String group, String version) {
		return (T) objectMap.get(serviceClass);
	}

	@Override
	public <T> boolean serviceExport(Class<T> interfaceClass, Object object, String group, String version, int port) {
		objectMap.put(interfaceClass, object);
		return true;
	}
}
