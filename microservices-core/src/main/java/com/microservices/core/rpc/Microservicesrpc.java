package com.microservices.core.rpc;

public interface Microservicesrpc {

	public <T> T serviceObtain(Class<T> serviceClass, String group, String version);

	public <T> boolean serviceExport(Class<T> interfaceClass, Object object, String group, String version, int port);

	public void onInitBefore();

	public void onInited();
}
