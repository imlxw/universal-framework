package com.microservices.core.rpc.motan;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.microservices.core.rpc.MicroservicesrpcBase;
import com.microservices.exception.MicroservicesIllegalConfigException;
import com.microservices.utils.StringUtils;
import com.weibo.api.motan.common.MotanConstants;
import com.weibo.api.motan.config.ProtocolConfig;
import com.weibo.api.motan.config.RefererConfig;
import com.weibo.api.motan.config.RegistryConfig;
import com.weibo.api.motan.config.ServiceConfig;
import com.weibo.api.motan.util.MotanSwitcherUtil;

public class MicroservicesMotanrpc extends MicroservicesrpcBase {

	private RegistryConfig registryConfig;
	private ProtocolConfig protocolConfig;

	private static final Map<String, Object> singletons = new ConcurrentHashMap<>();

	public MicroservicesMotanrpc() {

		registryConfig = new RegistryConfig();
		registryConfig.setCheck(String.valueOf(getRpcConfig().isRegistryCheck()));

		/**
		 * 注册中心的调用模式
		 */
		if (getRpcConfig().isRegistryCallMode()) {

			registryConfig.setRegProtocol(getRpcConfig().getRegistryType());
			registryConfig.setAddress(getRpcConfig().getRegistryAddress());
			registryConfig.setName(getRpcConfig().getRegistryName());
		}

		/**
		 * 直连模式
		 */
		else if (getRpcConfig().isRedirectCallMode()) {
			registryConfig.setRegProtocol("local");
		}

		protocolConfig = new ProtocolConfig();
		protocolConfig.setId("motan");
		protocolConfig.setName("motan");

		if (StringUtils.isNotBlank(getRpcConfig().getProxy())) {
			protocolConfig.setFilter(getRpcConfig().getProxy());
		} else {
			protocolConfig.setFilter("microservicesHystrix,microservicesOpentracing");
		}

		if (StringUtils.isNotBlank(getRpcConfig().getSerialization())) {
			protocolConfig.setSerialization(getRpcConfig().getSerialization());
		}

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

		RefererConfig<T> refererConfig = new RefererConfig<T>();

		// 设置接口及实现类
		refererConfig.setInterface(serviceClass);

		// 配置服务的group以及版本号
		refererConfig.setGroup(group);
		refererConfig.setVersion(version);
		refererConfig.setRequestTimeout(getRpcConfig().getRequestTimeOut());
		refererConfig.setProtocol(protocolConfig);

		if (StringUtils.isNotBlank(getRpcConfig().getProxy())) {
			refererConfig.setProxy(getRpcConfig().getProxy());
		} else {
			refererConfig.setProxy("microservices");
		}

		refererConfig.setCheck(String.valueOf(getRpcConfig().isConsumerCheck()));

		/**
		 * 注册中心模式
		 */
		if (getRpcConfig().isRegistryCallMode()) {
			refererConfig.setRegistry(registryConfig);
		}

		/**
		 * 直连模式
		 */
		else if (getRpcConfig().isRedirectCallMode()) {
			if (StringUtils.isBlank(getRpcConfig().getDirectUrl())) {
				throw new MicroservicesIllegalConfigException("directUrl must not be null if you use redirect call mode，please config microservices.rpc.directUrl value");
			}
			refererConfig.setDirectUrl(getRpcConfig().getDirectUrl());
		}

		object = refererConfig.getRef();

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

		synchronized (this) {

			MotanSwitcherUtil.setSwitcherValue(MotanConstants.REGISTRY_HEARTBEAT_SWITCHER, false);

			ServiceConfig<T> motanServiceConfig = new ServiceConfig<T>();
			motanServiceConfig.setRegistry(registryConfig);

			motanServiceConfig.setProtocol(protocolConfig);

			// 设置接口及实现类
			motanServiceConfig.setInterface(interfaceClass);
			motanServiceConfig.setRef((T) object);

			// 配置服务的group以及版本号
			if (StringUtils.isNotBlank(getRpcConfig().getHost())) {
				motanServiceConfig.setHost(getRpcConfig().getHost());
			}
			motanServiceConfig.setGroup(group);
			motanServiceConfig.setVersion(version);

			motanServiceConfig.setShareChannel(true);
			motanServiceConfig.setExport(String.format("motan:%s", port));
			motanServiceConfig.setCheck(String.valueOf(getRpcConfig().isProviderCheck()));

			motanServiceConfig.export();

			MotanSwitcherUtil.setSwitcherValue(MotanConstants.REGISTRY_HEARTBEAT_SWITCHER, true);
		}

		return true;
	}

}
