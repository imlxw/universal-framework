package com.microservices.core.cache;

import java.util.List;

import com.jfinal.plugin.ehcache.IDataLoader;

public interface MicroservicesCache extends com.jfinal.plugin.activerecord.cache.ICache {

	@Override
	public <T> T get(String cacheName, Object key);

	@Override
	public void put(String cacheName, Object key, Object value);

	public void put(String cacheName, Object key, Object value, int liveSeconds);

	public List getKeys(String cacheName);

	@Override
	public void remove(String cacheName, Object key);

	@Override
	public void removeAll(String cacheName);

	public <T> T get(String cacheName, Object key, IDataLoader dataLoader);

	public <T> T get(String cacheName, Object key, IDataLoader dataLoader, int liveSeconds);

	public Integer getTtl(String cacheName, Object key);

	public void setTtl(String cacheName, Object key, int seconds);

}
