package com.microservices.core.rpc.dubbo;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.alibaba.dubbo.config.ApplicationConfig;
import com.alibaba.dubbo.config.ProtocolConfig;
import com.alibaba.dubbo.config.ReferenceConfig;
import com.alibaba.dubbo.config.RegistryConfig;
import com.alibaba.dubbo.config.ServiceConfig;
import com.microservices.Microservices;
import com.microservices.core.rpc.MicroservicesrpcBase;
import com.microservices.exception.MicroservicesIllegalConfigException;
import com.microservices.utils.StringUtils;

/**
 * 添加dubbo的支持
 */
public class MicroservicesDubborpc extends MicroservicesrpcBase {

	private static final Map<String, Object> singletons = new ConcurrentHashMap<>();

	private MicroservicesDubborpcConfig dubboConfig;
	private RegistryConfig registryConfig;

	public MicroservicesDubborpc() {
		dubboConfig = Microservices.config(MicroservicesDubborpcConfig.class);

		registryConfig = new RegistryConfig();
		registryConfig.setCheck(getRpcConfig().isRegistryCheck());

		if (getRpcConfig().getRegistryFile() != null) {
			registryConfig.setFile(getRpcConfig().getRegistryFile());
		}

		/**
		 * 注册中心的调用模式
		 */
		if (getRpcConfig().isRegistryCallMode()) {

			registryConfig.setProtocol(getRpcConfig().getRegistryType());
			registryConfig.setAddress(getRpcConfig().getRegistryAddress());
			registryConfig.setUsername(getRpcConfig().getRegistryUserName());
			registryConfig.setPassword(getRpcConfig().getRegistryPassword());
		}
		/**
		 * 直连模式
		 */
		else if (getRpcConfig().isRedirectCallMode()) {
			registryConfig.setAddress(RegistryConfig.NO_AVAILABLE);
		}
	}

	private ApplicationConfig createApplicationConfig(String group) {
		ApplicationConfig applicationConfig = new ApplicationConfig();
		applicationConfig.setName(group);
		if (dubboConfig.getQosEnable() != null && dubboConfig.getQosEnable()) {
			applicationConfig.setQosEnable(true);
			applicationConfig.setQosPort(dubboConfig.getQosPort());
			applicationConfig.setQosAcceptForeignIp(dubboConfig.getQosAcceptForeignIp());
		} else {
			applicationConfig.setQosEnable(false);
		}

		return applicationConfig;
	}

	@Override
	public <T> T serviceObtain(Class<T> serviceClass, String group, String version) {

		if (StringUtils.isBlank(group)) {
			group = getRpcConfig().getDefaultGroup();
		}

		String key = String.format("%s:%s:%s", serviceClass.getName(), group, version);

		T object = (T) singletons.get(key);
		if (object != null) {
			return object;
		}

		// 注意：ReferenceConfig为重对象，内部封装了与注册中心的连接，以及与服务提供方的连接
		// 引用远程服务
		// 此实例很重，封装了与注册中心的连接以及与提供者的连接，请自行缓存，否则可能造成内存和连接泄漏
		ReferenceConfig<T> reference = new ReferenceConfig<T>();
		reference.setApplication(createApplicationConfig(group));
		reference.setInterface(serviceClass);
		reference.setVersion(version);
		reference.setTimeout(getRpcConfig().getRequestTimeOut());
		reference.setGroup(group);

		if (StringUtils.isNotBlank(getRpcConfig().getProxy())) {
			reference.setProxy(getRpcConfig().getProxy());
		} else {
			// 设置 microservices 代理，目的是为了方便 Hystrix 的降级控制和统计
			reference.setProxy("microservices");
		}

		if (StringUtils.isNotBlank(getRpcConfig().getFilter())) {
			reference.setFilter(getRpcConfig().getFilter());
		} else {
			// 默认情况下用于 OpenTracing 的追踪
			reference.setFilter("microservicesConsumerOpentracing");
		}

		reference.setCheck(getRpcConfig().isConsumerCheck());

		/**
		 * 注册中心的调用模式
		 */
		if (getRpcConfig().isRegistryCallMode()) {
			reference.setRegistry(registryConfig); // 多个注册中心可以用setRegistries()
		}

		/**
		 * 直连调用模式
		 */
		else if (getRpcConfig().isRedirectCallMode()) {
			if (StringUtils.isBlank(getRpcConfig().getDirectUrl())) {
				throw new MicroservicesIllegalConfigException("directUrl must not be null if you use redirect call mode，please config microservices.rpc.directUrl value");
			}
			reference.setUrl(getRpcConfig().getDirectUrl());
		}

		// 注意：此代理对象内部封装了所有通讯细节，对象较重，请缓存复用
		object = reference.get();

		if (object != null) {
			singletons.put(key, object);
		}

		return object;
	}

	@Override
	public <T> boolean serviceExport(Class<T> interfaceClass, Object object, String group, String version, int port) {

		if (StringUtils.isBlank(group)) {
			group = getRpcConfig().getDefaultGroup();
		}

		ProtocolConfig protocolConfig = dubboConfig.newProtocolConfig();

		if (protocolConfig.getHost() == null && getRpcConfig().getHost() != null) {
			protocolConfig.setHost(getRpcConfig().getHost());
		}

		if (protocolConfig.getSerialization() == null && getRpcConfig().getSerialization() != null) {
			protocolConfig.setSerialization(getRpcConfig().getSerialization());
		}

		if (protocolConfig.getPort() == null && getRpcConfig().getDefaultPort() != null) {
			protocolConfig.setPort(getRpcConfig().getDefaultPort());
		}

		if (port > 0) {
			protocolConfig.setPort(port);
		}

		// 此实例很重，封装了与注册中心的连接，请自行缓存，否则可能造成内存和连接泄漏
		ServiceConfig<T> service = new ServiceConfig<T>();
		service.setApplication(createApplicationConfig(group));
		service.setGroup(group);
		service.setRegistry(registryConfig); // 多个注册中心可以用setRegistries()

		service.setProtocol(protocolConfig); // 多个协议可以用setProtocols()
		service.setInterface(interfaceClass);
		service.setRef((T) object);
		service.setVersion(version);
		service.setProxy(getRpcConfig().getProxy());
		service.setFilter("microservicesProviderOpentracing");

		// 暴露及注册服务
		service.export();

		return true;
	}
}
