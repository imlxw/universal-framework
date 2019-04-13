package com.microservices.core.spi;

import java.util.Iterator;
import java.util.ServiceLoader;

/**
 * SPI 扩展加载器
 * <p>
 * 使用方法：
 * <p>
 * 第一步：编写支持扩展点的类，例如MyMicroservicesRpc extends Microservicesrpc。 第二步：给该类添加上注解 MicroservicesSpi， 例如 @MicroservicesSpi("myrpc") MyMicroservicesRpc extends Microservicesrpc ... 第三步：给microservices.properties配置上类型，microservices.rpc.type = myrpc
 * <p>
 * 通过这三步，就可以扩展自己的Microservicesrpc实现
 */
public class MicroservicesSpiLoader {

	/**
	 * 通过 SPI 去加载相应的扩展子类
	 *
	 * @param clazz
	 * @param spiName
	 * @param <T>
	 * @return
	 */
	public static <T> T load(Class<T> clazz, String spiName) {
		ServiceLoader<T> serviceLoader = ServiceLoader.load(clazz);
		Iterator<T> iterator = serviceLoader.iterator();
		while (iterator.hasNext()) {
			T t = iterator.next();
			if (spiName == null) {
				return t;
			}
			MicroservicesSpi spi = t.getClass().getAnnotation(MicroservicesSpi.class);
			if (spi == null) {
				continue;
			}
			if (spiName.equals(spi.value())) {
				return t;
			}
		}
		return null;
	}
}
