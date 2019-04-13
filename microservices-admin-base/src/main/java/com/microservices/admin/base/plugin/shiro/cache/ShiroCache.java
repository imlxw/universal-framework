package com.microservices.admin.base.plugin.shiro.cache;

import com.microservices.Microservices;
import org.apache.shiro.cache.Cache;
import org.apache.shiro.cache.CacheException;
import org.apache.shiro.util.CollectionUtils;

import java.util.*;

/**
 * Shiro 缓存，使用microservices cache
 * 
 * @param <K>
 * @param <V>
 */
public class ShiroCache<K, V> implements Cache<K, V> {

	public ShiroCache(String cacheName) {
		this.cacheName = cacheName;
	}

	private String cacheName;

	@Override
	public V get(K key) throws CacheException {
		return Microservices.me().getCache().get(cacheName, key);
	}

	@Override
	public V put(K key, V value) throws CacheException {
		Microservices.me().getCache().put(cacheName, key, value);
		return value;
	}

	@Override
	public V remove(K key) throws CacheException {
		V value = Microservices.me().getCache().get(cacheName, key);
		Microservices.me().getCache().remove(cacheName, key);
		return value;
	}

	@Override
	public void clear() throws CacheException {
		Microservices.me().getCache().removeAll(cacheName);
	}

	@Override
	public int size() {
		return Microservices.me().getCache().getKeys(cacheName).size();
	}

	@Override
	public Set<K> keys() {
		return (Set<K>) Microservices.me().getCache().getKeys(cacheName);
	}

	@Override
	public Collection<V> values() {
		Collection<V> values = Collections.emptyList();
		List keys = Microservices.me().getCache().getKeys(cacheName);

		if (!CollectionUtils.isEmpty(keys)) {
			values = new ArrayList<V>(keys.size());
			for (Object key : keys) {
				V value = Microservices.me().getCache().get(cacheName, key);
				if (value != null) {
					values.add(value);
				}
			}
		}

		return values;
	}

}
