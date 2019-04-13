package com.microservices.core.cache.j2cache;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.jfinal.plugin.ehcache.IDataLoader;
import com.microservices.core.cache.MicroservicesCache;
import com.microservices.exception.MicroservicesException;

import net.oschina.j2cache.CacheObject;
import net.oschina.j2cache.J2Cache;

/**
 * @version V1.0
 * @Package com.microservices.core.cache.j2cache
 */
public class J2cacheImpl implements MicroservicesCache {

	@Override
	public <T> T get(String cacheName, Object key) {
		CacheObject cacheObject = J2Cache.getChannel().get(cacheName, key.toString());
		return cacheObject != null ? (T) cacheObject.getValue() : null;
	}

	@Override
	public void put(String cacheName, Object key, Object value) {
		J2Cache.getChannel().set(cacheName, key.toString(), value);
	}

	@Override
	public void put(String cacheName, Object key, Object value, int liveSeconds) {
		J2Cache.getChannel().set(cacheName, key.toString(), value, liveSeconds);
	}

	@Override
	public List getKeys(String cacheName) {
		Collection keys = J2Cache.getChannel().keys(cacheName);
		return keys != null ? new ArrayList(keys) : null;
	}

	@Override
	public void remove(String cacheName, Object key) {
		J2Cache.getChannel().evict(cacheName, key.toString());
	}

	@Override
	public void removeAll(String cacheName) {
		J2Cache.getChannel().clear(cacheName);
	}

	@Override
	public <T> T get(String cacheName, Object key, IDataLoader dataLoader) {
		Object value = get(cacheName, key);
		if (value == null) {
			value = dataLoader.load();
			if (value != null) {
				put(cacheName, key, value);
			}
		}
		return (T) value;
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
		throw new MicroservicesException("getTtl not support in j2cache");
	}

	@Override
	public void setTtl(String cacheName, Object key, int seconds) {
		throw new MicroservicesException("setTtl not support in j2cache");
	}
}
