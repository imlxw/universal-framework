package com.microservices.aop.interceptor.cache;

import java.lang.reflect.Method;

import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;
import com.microservices.Microservices;
import com.microservices.core.cache.annotation.CachePut;
import com.microservices.exception.MicroservicesAssert;
import com.microservices.utils.StringUtils;

/**
 * 缓存设置拦截器
 */
public class MicroservicesCachePutInterceptor implements Interceptor {

	// @Override
	// public Object invoke(MethodInvocation methodInvocation) throws Throwable {
	//
	// Class targetClass = methodInvocation.getThis().getClass();
	// Method method = methodInvocation.getMethod();
	//
	// Object result = methodInvocation.proceed();
	//
	// CachePut cachePut = method.getAnnotation(CachePut.class);
	// if (cachePut == null) {
	// return result;
	// }
	//
	// String unlessString = cachePut.unless();
	// if (StringUtils.isNotBlank(unlessString)) {
	// unlessString = String.format("#(%s)", unlessString);
	// String unlessBoolString = Kits.engineRender(unlessString, method, methodInvocation.getArguments());
	// if ("true".equals(unlessBoolString)) {
	// return result;
	// }
	// }
	//
	//
	// String cacheName = cachePut.name();
	// MicroservicesAssert.assertTrue(StringUtils.isNotBlank(cacheName),
	// String.format("CachePut.name() must not empty in method [%s]!!!", targetClass.getName() + "#" + method.getName()));
	//
	// String cacheKey = Kits.buildCacheKey(cachePut.key(), targetClass, method, methodInvocation.getArguments());
	//
	// if (cachePut.liveSeconds() > 0) {
	// Microservices.me().getCache().put(cacheName, cacheKey, result, cachePut.liveSeconds());
	// } else {
	// Microservices.me().getCache().put(cacheName, cacheKey, result);
	// }
	// return result;
	// }

	@Override
	public void intercept(Invocation inv) {

		inv.invoke();

		Method method = inv.getMethod();
		CachePut cachePut = method.getAnnotation(CachePut.class);
		if (cachePut == null) {
			return;
		}

		Object result = inv.getReturnValue();

		String unlessString = cachePut.unless();
		if (StringUtils.isNotBlank(unlessString)) {
			unlessString = String.format("#(%s)", unlessString);
			String unlessBoolString = Kits.engineRender(unlessString, method, inv.getArgs());
			if ("true".equals(unlessBoolString)) {
				return;
			}
		}

		Class targetClass = inv.getTarget().getClass();
		String cacheName = cachePut.name();
		MicroservicesAssert.assertTrue(StringUtils.isNotBlank(cacheName), String.format("CachePut.name()  must not empty in method [%s]!!!", targetClass.getName() + "#" + method.getName()));

		String cacheKey = Kits.buildCacheKey(cachePut.key(), targetClass, method, inv.getArgs());

		if (cachePut.liveSeconds() > 0) {
			Microservices.me().getCache().put(cacheName, cacheKey, result, cachePut.liveSeconds());
		} else {
			Microservices.me().getCache().put(cacheName, cacheKey, result);
		}
	}
}
