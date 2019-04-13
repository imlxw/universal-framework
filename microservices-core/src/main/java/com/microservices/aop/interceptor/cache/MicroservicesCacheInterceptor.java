package com.microservices.aop.interceptor.cache;

import java.lang.reflect.Method;

import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;
import com.microservices.Microservices;
import com.microservices.core.cache.annotation.Cacheable;
import com.microservices.exception.MicroservicesAssert;
import com.microservices.utils.StringUtils;

/**
 * 缓存操作的拦截器
 */
public class MicroservicesCacheInterceptor implements Interceptor {

	private static final String NULL_VALUE = "NULL_VALUE";

	@Override
	public void intercept(Invocation inv) {

		Method method = inv.getMethod();
		Cacheable cacheable = method.getAnnotation(Cacheable.class);
		if (cacheable == null) {
			inv.invoke();
			return;
		}

		String unlessString = cacheable.unless();
		if (Kits.isUnless(unlessString, method, inv.getArgs())) {
			inv.invoke();
			return;
		}

		Class targetClass = inv.getTarget().getClass();
		String cacheName = cacheable.name();
		MicroservicesAssert.assertTrue(StringUtils.isNotBlank(cacheName), String.format("Cacheable.name()  must not empty in method [%s]!!!", targetClass.getName() + "#" + method.getName()));

		String cacheKey = Kits.buildCacheKey(cacheable.key(), targetClass, method, inv.getArgs());

		Object data = Microservices.me().getCache().get(cacheName, cacheKey);
		if (data != null) {
			if (NULL_VALUE.equals(data)) {
				inv.setReturnValue(null);
			} else {
				inv.setReturnValue(data);
			}
			return;
		}

		inv.invoke();

		data = inv.getReturnValue();

		if (data != null) {
			cacheData(cacheable, cacheName, cacheKey, data);
		} else if (data == null && cacheable.nullCacheEnable()) {
			cacheData(cacheable, cacheName, cacheKey, NULL_VALUE);
		}
	}

	private void cacheData(Cacheable cacheable, String cacheName, String cacheKey, Object data) {
		if (cacheable.liveSeconds() > 0) {
			Microservices.me().getCache().put(cacheName, cacheKey, data, cacheable.liveSeconds());
		} else {
			Microservices.me().getCache().put(cacheName, cacheKey, data);
		}
	}

}
