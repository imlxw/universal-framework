package com.microservices.aop.interceptor.cache;

import java.lang.reflect.Method;

import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;
import com.microservices.core.cache.annotation.CacheEvict;

/**
 * 清除缓存操作的拦截器
 */
public class MicroservicesCacheEvictInterceptor implements Interceptor {

	@Override
	public void intercept(Invocation inv) {

		Method method = inv.getMethod();

		CacheEvict cacheEvict = method.getAnnotation(CacheEvict.class);
		if (cacheEvict == null) {
			inv.invoke();
			return;
		}

		Class targetClass = inv.getTarget().getClass();

		if (cacheEvict.beforeInvocation()) {
			Kits.doCacheEvict(inv.getArgs(), targetClass, method, cacheEvict);
		}

		inv.invoke();

		if (!cacheEvict.beforeInvocation()) {
			Kits.doCacheEvict(inv.getArgs(), targetClass, method, cacheEvict);
		}
	}
}
