package com.microservices.core.rpc;

import java.io.Serializable;
import java.util.List;

import com.microservices.Microservices;
import com.microservices.core.mq.MicroservicesmqMessageListener;
import com.microservices.core.rpc.annotation.MicroservicesrpcService;
import com.microservices.core.rpc.dubbo.MicroservicesDubborpc;
import com.microservices.core.rpc.local.MicroservicesLocalrpc;
import com.microservices.core.rpc.motan.MicroservicesMotanrpc;
import com.microservices.core.rpc.zbus.MicroservicesZbusrpc;
import com.microservices.core.spi.MicroservicesSpiLoader;
import com.microservices.event.MicroservicesEventListener;
import com.microservices.exception.MicroservicesAssert;
import com.microservices.utils.ArrayUtils;
import com.microservices.utils.ClassKits;
import com.microservices.utils.ClassScanner;
import com.microservices.utils.StringUtils;

public class MicroservicesrpcManager {

	private static MicroservicesrpcManager manager = new MicroservicesrpcManager();

	public static MicroservicesrpcManager me() {
		return manager;
	}

	private Microservicesrpc microservicesrpc;
	private MicroservicesrpcConfig config = Microservices.config(MicroservicesrpcConfig.class);

	public Microservicesrpc getMicroservicesrpc() {
		if (microservicesrpc == null) {
			microservicesrpc = createMicroservicesrpc();
		}
		return microservicesrpc;
	}

	static Class[] default_excludes = new Class[] { MicroservicesEventListener.class, MicroservicesmqMessageListener.class, Serializable.class };

	public void init() {

		getMicroservicesrpc().onInitBefore();

		List<Class> classes = ClassScanner.scanClass(true);
		if (ArrayUtils.isNullOrEmpty(classes)) {
			return;
		}

		for (Class clazz : classes) {
			MicroservicesrpcService rpcService = (MicroservicesrpcService) clazz.getAnnotation(MicroservicesrpcService.class);
			if (rpcService == null)
				continue;

			String group = StringUtils.isBlank(rpcService.group()) ? config.getDefaultGroup() : rpcService.group();
			String version = StringUtils.isBlank(rpcService.version()) ? config.getDefaultVersion() : rpcService.version();
			int port = rpcService.port() <= 0 ? config.getDefaultPort() : rpcService.port();

			Class[] inters = clazz.getInterfaces();
			MicroservicesAssert.assertFalse(inters == null || inters.length == 0, String.format("class[%s] has no interface, can not use @MicroservicesrpcService", clazz));

			// 对某些系统的类 进行排除，例如：Serializable 等
			Class[] excludes = ArrayUtils.concat(default_excludes, rpcService.exclude());
			for (Class inter : inters) {
				boolean isContinue = false;
				for (Class ex : excludes) {
					if (ex.isAssignableFrom(inter)) {
						isContinue = true;
						break;
					}
				}
				if (isContinue) {
					continue;
				}
				getMicroservicesrpc().serviceExport(inter, Microservices.bean(clazz), group, version, port);
			}
		}

		getMicroservicesrpc().onInited();
	}

	private Microservicesrpc createMicroservicesrpc() {

		switch (config.getType()) {
			case MicroservicesrpcConfig.TYPE_MOTAN:
				return new MicroservicesMotanrpc();
			case MicroservicesrpcConfig.TYPE_LOCAL:
				return new MicroservicesLocalrpc();
			case MicroservicesrpcConfig.TYPE_DUBBO:
				return new MicroservicesDubborpc();
			case MicroservicesrpcConfig.TYPE_ZBUS:
				return new MicroservicesZbusrpc();
			default:
				return MicroservicesSpiLoader.load(Microservicesrpc.class, config.getType());
		}
	}

	private MicroservicesrpcHystrixFallbackListener fallbackListener = null;

	public MicroservicesrpcHystrixFallbackListener getHystrixFallbackListener() {

		if (fallbackListener != null) {
			return fallbackListener;
		}

		if (!StringUtils.isBlank(config.getHystrixFallbackListener())) {
			fallbackListener = ClassKits.newInstance(config.getHystrixFallbackListener());

		}

		if (fallbackListener == null) {
			fallbackListener = new MicroservicesrpcHystrixFallbackListenerDefault();
		}

		return fallbackListener;
	}

}
