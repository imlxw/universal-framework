package com.microservices.core.cache;

import com.microservices.Microservices;
import com.microservices.core.cache.ehcache.MicroservicesEhcacheImpl;
import com.microservices.core.cache.ehredis.MicroservicesEhredisCacheImpl;
import com.microservices.core.cache.j2cache.J2cacheImpl;
import com.microservices.core.cache.redis.MicroservicesRedisCacheImpl;
import com.microservices.core.spi.MicroservicesSpiLoader;

public class MicroservicesCacheManager {

	private static MicroservicesCacheManager me = new MicroservicesCacheManager();

	private MicroservicesCacheManager() {
	}

	private MicroservicesCache microservicesCache;

	public static MicroservicesCacheManager me() {
		return me;
	}

	public MicroservicesCache getCache() {
		if (microservicesCache == null) {
			MicroservicesCacheConfig config = Microservices.config(MicroservicesCacheConfig.class);
			microservicesCache = buildCache(config);
		}
		return microservicesCache;
	}

	public MicroservicesCache getCache(String type) {
		MicroservicesCacheConfig cacheConfig = new MicroservicesCacheConfig();
		cacheConfig.setType(type);
		return buildCache(cacheConfig);
	}

	private MicroservicesCache buildCache(MicroservicesCacheConfig config) {

		switch (config.getType()) {
			case MicroservicesCacheConfig.TYPE_EHCACHE:
				return new MicroservicesEhcacheImpl();
			case MicroservicesCacheConfig.TYPE_REDIS:
				return new MicroservicesRedisCacheImpl();
			case MicroservicesCacheConfig.TYPE_EHREDIS:
				return new MicroservicesEhredisCacheImpl();
			case MicroservicesCacheConfig.TYPE_J2CACHE:
				return new J2cacheImpl();
			default:
				return MicroservicesSpiLoader.load(MicroservicesCache.class, config.getType());
		}
	}
}
