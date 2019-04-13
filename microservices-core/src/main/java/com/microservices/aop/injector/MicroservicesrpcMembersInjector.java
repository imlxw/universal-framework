package com.microservices.aop.injector;

import java.lang.reflect.Field;

import com.google.inject.MembersInjector;
import com.jfinal.log.Log;
import com.microservices.Microservices;
import com.microservices.core.rpc.MicroservicesrpcConfig;
import com.microservices.core.rpc.annotation.MicroservicesrpcService;
import com.microservices.utils.StringUtils;

/**
 * RPC 的注入器，用来初始化RPC对象
 */
public class MicroservicesrpcMembersInjector implements MembersInjector {

	private static Log log = Log.getLog(MicroservicesrpcMembersInjector.class);

	private Field field;
	private static MicroservicesrpcConfig config = Microservices.config(MicroservicesrpcConfig.class);

	public MicroservicesrpcMembersInjector(Field field) {
		this.field = field;
	}

	@Override
	public void injectMembers(Object instance) {
		Object impl = null;
		MicroservicesrpcService microservicesrpcService = field.getAnnotation(MicroservicesrpcService.class);

		String group = StringUtils.isBlank(microservicesrpcService.group()) ? config.getDefaultGroup() : microservicesrpcService.group();
		String version = StringUtils.isBlank(microservicesrpcService.version()) ? config.getDefaultVersion() : microservicesrpcService.version();

		try {
			impl = Microservices.service(field.getType(), group, version);
		} catch (Throwable e) {
			log.error(e.toString(), e);
		}

		if (impl == null) {
			return;
		}

		try {
			field.setAccessible(true);
			field.set(instance, impl);
		} catch (Throwable e) {
			log.error(e.toString(), e);
		}
	}
}
