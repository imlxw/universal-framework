package com.microservices.core.cache.redis;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.jfinal.plugin.ehcache.IDataLoader;
import com.microservices.Microservices;
import com.microservices.component.redis.MicroservicesRedis;
import com.microservices.component.redis.MicroservicesRedisManager;
import com.microservices.core.cache.MicroservicesCacheBase;
import com.microservices.exception.MicroservicesIllegalConfigException;

public class MicroservicesRedisCacheImpl extends MicroservicesCacheBase {

	private MicroservicesRedis redis;

	public MicroservicesRedisCacheImpl() {
		MicroservicesRedisCacheConfig redisConfig = Microservices.config(MicroservicesRedisCacheConfig.class);
		if (redisConfig.isConfigOk()) {
			redis = MicroservicesRedisManager.me().getRedis(redisConfig);
		} else {
			redis = Microservices.me().getRedis();
		}

		if (redis == null) {
			throw new MicroservicesIllegalConfigException("can not get redis, please check your microservices.properties , please correct config microservices.cache.redis.host or microservices.redis.host ");
		}
	}

	@Override
	public <T> T get(String cacheName, Object key) {
		return redis.get(buildKey(cacheName, key));
	}

	@Override
	public void put(String cacheName, Object key, Object value) {
		if (value == null) {
			// if value is null : java.lang.NullPointerException: null at redis.clients.jedis.Protocol.sendCommand(Protocol.java:99)
			return;
		}
		redis.set(buildKey(cacheName, key), value);
	}

	@Override
	public void put(String cacheName, Object key, Object value, int liveSeconds) {
		if (value == null) {
			// if value is null : java.lang.NullPointerException: null at redis.clients.jedis.Protocol.sendCommand(Protocol.java:99)
			return;
		}
		if (liveSeconds <= 0) {
			put(cacheName, key, value);
			return;
		}

		redis.setex(buildKey(cacheName, key), liveSeconds, value);
	}

	@Override
	public List getKeys(String cacheName) {
		Set<String> keyset = redis.keys(cacheName + ":*");
		if (keyset == null || keyset.size() == 0) {
			return null;
		}
		List<String> keys = new ArrayList<>(keyset);
		for (int i = 0; i < keys.size(); i++) {
			keys.set(i, keys.get(i).substring(cacheName.length() + 3));
		}
		return keys;
	}

	@Override
	public void remove(String cacheName, Object key) {
		redis.del(buildKey(cacheName, key));
	}

	@Override
	public void removeAll(String cacheName) {
		String[] keys = new String[] {};
		keys = redis.keys(cacheName + ":*").toArray(keys);
		if (keys != null && keys.length > 0) {
			redis.del(keys);
		}
	}

	@Override
	public <T> T get(String cacheName, Object key, IDataLoader dataLoader) {
		Object data = get(cacheName, key);
		if (data == null) {
			data = dataLoader.load();
			put(cacheName, key, data);
		}
		return (T) data;
	}

	private String buildKey(String cacheName, Object key) {
		if (key instanceof Number)
			return String.format("%s:I:%s", cacheName, key);
		else {
			Class keyClass = key.getClass();
			if (String.class.equals(keyClass) || StringBuffer.class.equals(keyClass) || StringBuilder.class.equals(keyClass)) {
				return String.format("%s:S:%s", cacheName, key);
			}
		}
		return String.format("%s:O:%s", cacheName, key);
	}

	@Override
	public <T> T get(String cacheName, Object key, IDataLoader dataLoader, int liveSeconds) {
		if (liveSeconds <= 0) {
			return get(cacheName, key, dataLoader);
		}

		Object data = get(cacheName, key);
		if (data == null) {
			data = dataLoader.load();
			put(cacheName, key, data, liveSeconds);
		}
		return (T) data;
	}

	@Override
	public Integer getTtl(String cacheName, Object key) {
		Long ttl = redis.ttl(buildKey(cacheName, key));
		return ttl != null ? ttl.intValue() : null;
	}

	@Override
	public void setTtl(String cacheName, Object key, int seconds) {
		redis.expire(buildKey(cacheName, key), seconds);
	}

	public MicroservicesRedis getRedis() {
		return redis;
	}

}
