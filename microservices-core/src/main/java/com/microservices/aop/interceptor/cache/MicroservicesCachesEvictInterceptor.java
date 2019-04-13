package com.microservices.aop.interceptor.cache;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;
import com.jfinal.log.Log;
import com.microservices.core.cache.annotation.CacheEvict;
import com.microservices.core.cache.annotation.CachesEvict;

/**
 * 清除缓存操作的拦截器
 */
public class MicroservicesCachesEvictInterceptor implements Interceptor {
	private static final Log LOG = Log.getLog(MicroservicesCachesEvictInterceptor.class);

	private void doCachesEvict(Object[] arguments, Class targetClass, Method method, List<CacheEvict> cacheEvicts) {
		for (CacheEvict evict : cacheEvicts) {
			try {
				Kits.doCacheEvict(arguments, targetClass, method, evict);
			} catch (Exception ex) {
				LOG.error(ex.toString(), ex);
			}
		}
	}

	@Override
	public void intercept(Invocation inv) {

		Method method = inv.getMethod();

		CachesEvict cachesEvict = method.getAnnotation(CachesEvict.class);
		if (cachesEvict == null) {
			inv.invoke();
			return;
		}

		CacheEvict[] evicts = cachesEvict.value();

		List<CacheEvict> beforeInvocations = new ArrayList<>();
		List<CacheEvict> afterInvocations = new ArrayList<>();

		for (CacheEvict evict : evicts) {
			if (evict.beforeInvocation()) {
				beforeInvocations.add(evict);
			} else {
				afterInvocations.add(evict);
			}
		}

		Class targetClass = inv.getTarget().getClass();

		doCachesEvict(inv.getArgs(), targetClass, method, beforeInvocations);
		inv.invoke();
		doCachesEvict(inv.getArgs(), targetClass, method, afterInvocations);
	}
}
