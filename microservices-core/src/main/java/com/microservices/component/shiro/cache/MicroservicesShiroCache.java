package com.microservices.component.shiro.cache;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.shiro.cache.Cache;
import org.apache.shiro.cache.CacheException;
import org.apache.shiro.util.CollectionUtils;

import com.microservices.Microservices;

/**
 * 自定义 shiro cache
 *
 * @param <K>
 * @param <V>
 */
public class MicroservicesShiroCache<K, V> implements Cache<K, V> {

	private String cacheName;

	public MicroservicesShiroCache(String cacheName) {
		this.cacheName = "shiroCache:" + cacheName;
	}

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
		Set<K> keys = keys();
		return keys == null ? 0 : keys.size();
	}

	@Override
	public Set<K> keys() {
		List list = Microservices.me().getCache().getKeys(cacheName);
		return list == null ? null : new HashSet<K>(list);
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
