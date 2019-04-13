package com.microservices.component.redis;

import com.microservices.Microservices;
import com.microservices.component.redis.jedis.MicroservicesJedisClusterImpl;
import com.microservices.component.redis.jedis.MicroservicesJedisImpl;
import com.microservices.exception.MicroservicesException;

/**
 * 参考： com.jfinal.plugin.redis MicroservicesRedis 命令文档: http://redisdoc.com/
 */
public class MicroservicesRedisManager {

	private static MicroservicesRedisManager manager = new MicroservicesRedisManager();

	private MicroservicesRedisManager() {
	}

	public static MicroservicesRedisManager me() {
		return manager;
	}

	private MicroservicesRedis redis;

	public MicroservicesRedis getRedis() {
		if (redis == null) {
			MicroservicesRedisConfig config = Microservices.config(MicroservicesRedisConfig.class);
			redis = getRedis(config);
		}

		return redis;
	}

	public MicroservicesRedis getRedis(MicroservicesRedisConfig config) {
		if (config == null || !config.isConfigOk()) {
			return null;
		}

		switch (config.getType()) {
			case MicroservicesRedisConfig.TYPE_JEDIS:
				return getJedisClinet(config);
			case MicroservicesRedisConfig.TYPE_LETTUCE:
				return getLettuceClient(config);
			case MicroservicesRedisConfig.TYPE_REDISSON:
				return getRedissonClient(config);
		}

		return null;

	}

	private MicroservicesRedis getJedisClinet(MicroservicesRedisConfig config) {
		if (config.isCluster()) {
			return new MicroservicesJedisClusterImpl(config);
		} else {
			return new MicroservicesJedisImpl(config);
		}
	}

	private MicroservicesRedis getLettuceClient(MicroservicesRedisConfig config) {
		throw new MicroservicesException("lettuce is not finished.");
	}

	private MicroservicesRedis getRedissonClient(MicroservicesRedisConfig config) {
		throw new MicroservicesException("redisson is not finished.");
	}

}
