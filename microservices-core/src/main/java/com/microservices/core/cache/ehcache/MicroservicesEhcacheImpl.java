package com.microservices.core.cache.ehcache;

import java.util.List;

import com.jfinal.kit.PathKit;
import com.jfinal.log.Log;
import com.jfinal.plugin.ehcache.IDataLoader;
import com.microservices.Microservices;
import com.microservices.core.cache.MicroservicesCacheBase;
import com.microservices.utils.StringUtils;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;
import net.sf.ehcache.event.CacheEventListener;

public class MicroservicesEhcacheImpl extends MicroservicesCacheBase {

	private CacheManager cacheManager;
	private static Object locker = new Object();

	private static final Log log = Log.getLog(MicroservicesEhcacheImpl.class);

	private CacheEventListener cacheEventListener;

	public MicroservicesEhcacheImpl() {
		MicroservicesEhCacheConfig config = Microservices.config(MicroservicesEhCacheConfig.class);
		if (StringUtils.isBlank(config.getConfigFileName())) {
			cacheManager = CacheManager.create();
		} else {
			String configPath = config.getConfigFileName();
			if (!configPath.startsWith("/")) {
				configPath = PathKit.getRootClassPath() + "/" + configPath;
			}
			cacheManager = CacheManager.create(configPath);
		}
	}

	public MicroservicesEhcacheImpl(CacheManager cacheManager) {
		this.cacheManager = cacheManager;
	}

	public CacheEventListener getCacheEventListener() {
		return cacheEventListener;
	}

	public void setCacheEventListener(CacheEventListener cacheEventListener) {
		this.cacheEventListener = cacheEventListener;
	}

	public Cache getOrAddCache(String cacheName) {
		Cache cache = cacheManager.getCache(cacheName);
		if (cache == null) {
			synchronized (locker) {
				cache = cacheManager.getCache(cacheName);
				if (cache == null) {
					log.warn("Could not find cache config [" + cacheName + "], using default.");
					cacheManager.addCacheIfAbsent(cacheName);
					cache = cacheManager.getCache(cacheName);
					if (cacheEventListener != null) {
						cache.getCacheEventNotificationService().registerListener(cacheEventListener);
					}
				}
			}
		}
		return cache;
	}

	@Override
	public List getKeys(String cacheName) {
		return getOrAddCache(cacheName).getKeys();
	}

	@Override
	public <T> T get(String cacheName, Object key) {
		Element element = getOrAddCache(cacheName).get(key);
		return element != null ? (T) element.getObjectValue() : null;
	}

	@Override
	public void put(String cacheName, Object key, Object value) {
		getOrAddCache(cacheName).put(new Element(key, value));
	}

	@Override
	public void put(String cacheName, Object key, Object value, int liveSeconds) {
		if (liveSeconds <= 0) {
			put(cacheName, key, value);
			return;
		}
		Element element = new Element(key, value);
		element.setTimeToLive(liveSeconds);
		getOrAddCache(cacheName).put(element);
	}

	@Override
	public void remove(String cacheName, Object key) {
		getOrAddCache(cacheName).remove(key);
	}

	@Override
	public void removeAll(String cacheName) {
		getOrAddCache(cacheName).removeAll();
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
		Element element = getOrAddCache(cacheName).get(key);
		return element != null ? element.getTimeToLive() : null;
	}

	@Override
	public void setTtl(String cacheName, Object key, int seconds) {
		Element element = getOrAddCache(cacheName).get(key);
		if (element == null) {
			return;
		}

		element.setTimeToLive(seconds);
		getOrAddCache(cacheName).put(element);
	}

	public CacheManager getCacheManager() {
		return cacheManager;
	}

}
