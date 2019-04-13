package com.microservices.component.shiro.cache;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import org.apache.shiro.cache.Cache;
import org.apache.shiro.cache.CacheException;
import org.apache.shiro.cache.CacheManager;

import com.google.common.cache.CacheBuilder;

/**
 * 封装 shiro cache manager 通过 shiro.ini 的进行配置，配置如下 ： shiroCacheManager = com.microservices.component.shiro.cache.MicroservicesShiroCacheManager securityManager.cacheManager = $shiroCacheManager
 */
public class MicroservicesShiroCacheManager implements CacheManager {

	private static final com.google.common.cache.Cache<String, Cache> guavaCache = CacheBuilder.newBuilder().expireAfterWrite(40, TimeUnit.MINUTES).expireAfterAccess(40, TimeUnit.MINUTES).build();

	@Override
	public <K, V> Cache<K, V> getCache(String name) throws CacheException {
		try {
			return guavaCache.get(name, new Callable<Cache>() {
				@Override
				public Cache call() throws Exception {
					return new MicroservicesShiroCache(name);
				}
			});
		} catch (ExecutionException e) {
			throw new CacheException(e);
		}
	}

}
