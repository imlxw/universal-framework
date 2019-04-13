package com.microservices.core.cache.ehredis;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import com.jfinal.plugin.ehcache.IDataLoader;
import com.microservices.Microservices;
import com.microservices.component.redis.MicroservicesRedis;
import com.microservices.core.cache.MicroservicesCacheBase;
import com.microservices.core.cache.ehcache.MicroservicesEhcacheImpl;
import com.microservices.core.cache.redis.MicroservicesRedisCacheImpl;
import com.microservices.core.serializer.ISerializer;
import com.microservices.utils.StringUtils;

import net.sf.ehcache.CacheException;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;
import net.sf.ehcache.event.CacheEventListener;
import redis.clients.jedis.BinaryJedisPubSub;

/**
 * 基于 ehcache和redis做的二级缓存 优点是：减少高并发下redis的io瓶颈
 */
public class MicroservicesEhredisCacheImpl extends MicroservicesCacheBase implements CacheEventListener {

	public static final String DEFAULT_NOTIFY_CHANNEL = "microservices_ehredis_channel";

	private MicroservicesEhcacheImpl ehcacheImpl;
	private MicroservicesRedisCacheImpl redisCacheImpl;
	private MicroservicesRedis redis;
	private ISerializer serializer;

	private String channel = DEFAULT_NOTIFY_CHANNEL;
	private String clientId;

	private LoadingCache<String, List> keysCache = Caffeine.newBuilder().expireAfterAccess(10, TimeUnit.MINUTES).expireAfterWrite(10, TimeUnit.MINUTES).build(key -> null);

	public MicroservicesEhredisCacheImpl() {
		this.ehcacheImpl = new MicroservicesEhcacheImpl();
		this.ehcacheImpl.setCacheEventListener(this);

		this.redisCacheImpl = new MicroservicesRedisCacheImpl();
		this.clientId = StringUtils.uuid();
		this.serializer = Microservices.me().getSerializer();

		this.redis = redisCacheImpl.getRedis();
		this.redis.subscribe(new BinaryJedisPubSub() {
			@Override
			public void onMessage(byte[] channel, byte[] message) {
				MicroservicesEhredisCacheImpl.this.onMessage((String) serializer.deserialize(channel), serializer.deserialize(message));
			}
		}, serializer.serialize(channel));
	}

	@Override
	public List getKeys(String cacheName) {
		List list = keysCache.getIfPresent(cacheName);
		if (list == null) {
			list = redisCacheImpl.getKeys(cacheName);
			if (list == null) {
				list = new ArrayList();
			}
			keysCache.put(cacheName, list);
		}
		return list;
	}

	@Override
	public <T> T get(String cacheName, Object key) {
		T value = ehcacheImpl.get(cacheName, key);
		if (value == null) {
			value = redisCacheImpl.get(cacheName, key);
			if (value != null) {
				Integer ttl = redisCacheImpl.getTtl(cacheName, key);
				if (ttl != null && ttl > 0) {
					ehcacheImpl.put(cacheName, key, value, ttl);
				} else {
					ehcacheImpl.put(cacheName, key, value);
				}
			}
		}
		return value;
	}

	@Override
	public void put(String cacheName, Object key, Object value) {
		try {
			ehcacheImpl.put(cacheName, key, value);
			redisCacheImpl.put(cacheName, key, value);
		} finally {
			publishMessage(MicroservicesEhredisMessage.ACTION_PUT, cacheName, key);
		}
	}

	@Override
	public void put(String cacheName, Object key, Object value, int liveSeconds) {
		if (liveSeconds <= 0) {
			put(cacheName, key, value);
			return;
		}
		try {
			ehcacheImpl.put(cacheName, key, value, liveSeconds);
			redisCacheImpl.put(cacheName, key, value, liveSeconds);
		} finally {
			publishMessage(MicroservicesEhredisMessage.ACTION_PUT, cacheName, key);
		}
	}

	@Override
	public void remove(String cacheName, Object key) {
		try {
			ehcacheImpl.remove(cacheName, key);
			redisCacheImpl.remove(cacheName, key);
		} finally {
			publishMessage(MicroservicesEhredisMessage.ACTION_REMOVE, cacheName, key);
		}
	}

	@Override
	public void removeAll(String cacheName) {
		try {
			ehcacheImpl.removeAll(cacheName);
			redisCacheImpl.removeAll(cacheName);
		} finally {
			publishMessage(MicroservicesEhredisMessage.ACTION_REMOVE_ALL, cacheName, null);
		}
	}

	@Override
	public <T> T get(String cacheName, Object key, IDataLoader dataLoader) {
		T value = get(cacheName, key);
		if (value != null) {
			return value;
		}

		value = (T) dataLoader.load();
		if (value != null) {
			put(cacheName, key, value);
		}
		return value;
	}

	@Override
	public <T> T get(String cacheName, Object key, IDataLoader dataLoader, int liveSeconds) {
		if (liveSeconds <= 0) {
			return get(cacheName, key, dataLoader);
		}

		T value = get(cacheName, key);
		if (value != null) {
			return value;
		}

		value = (T) dataLoader.load();
		if (value != null) {
			put(cacheName, key, value, liveSeconds);
		}
		return value;
	}

	@Override
	public Integer getTtl(String cacheName, Object key) {
		Integer ttl = ehcacheImpl.getTtl(cacheName, key);
		if (ttl == null) {
			ttl = redisCacheImpl.getTtl(cacheName, key);
		}
		return ttl;
	}

	@Override
	public void setTtl(String cacheName, Object key, int seconds) {
		try {
			ehcacheImpl.setTtl(cacheName, key, seconds);
			redisCacheImpl.setTtl(cacheName, key, seconds);
		} finally {
			publishMessage(MicroservicesEhredisMessage.ACTION_REMOVE, cacheName, key);
		}

	}

	private void publishMessage(int action, String cacheName, Object key) {
		clearKeysCache(cacheName);
		MicroservicesEhredisMessage message = new MicroservicesEhredisMessage(clientId, action, cacheName, key);
		redis.publish(serializer.serialize(channel), serializer.serialize(message));
	}

	private void clearKeysCache(String cacheName) {
		keysCache.invalidate(cacheName);
	}

	public void onMessage(String channel, Object obj) {

		MicroservicesEhredisMessage message = (MicroservicesEhredisMessage) obj;
		/**
		 * 不处理自己发送的消息
		 */
		if (clientId.equals(message.getClientId())) {
			return;
		}

		clearKeysCache(message.getCacheName());

		switch (message.getAction()) {
			case MicroservicesEhredisMessage.ACTION_PUT:
				ehcacheImpl.remove(message.getCacheName(), message.getKey());
				break;
			case MicroservicesEhredisMessage.ACTION_REMOVE:
				ehcacheImpl.remove(message.getCacheName(), message.getKey());
				break;
			case MicroservicesEhredisMessage.ACTION_REMOVE_ALL:
				ehcacheImpl.removeAll(message.getCacheName());
				break;
		}
	}

	public MicroservicesEhcacheImpl getEhcacheImpl() {
		return ehcacheImpl;
	}

	public MicroservicesRedisCacheImpl getRedisCacheImpl() {
		return redisCacheImpl;
	}

	@Override
	public void notifyElementRemoved(Ehcache cache, Element element) throws CacheException {
	}

	@Override
	public void notifyElementPut(Ehcache cache, Element element) throws CacheException {
	}

	@Override
	public void notifyElementUpdated(Ehcache cache, Element element) throws CacheException {
	}

	@Override
	public void notifyElementExpired(Ehcache cache, Element element) {
		clearKeysCache(cache.getName());
	}

	@Override
	public void notifyElementEvicted(Ehcache cache, Element element) {
	}

	@Override
	public void notifyRemoveAll(Ehcache cache) {
	}

	@Override
	public Object clone() throws CloneNotSupportedException {
		throw new CloneNotSupportedException();
	}

	@Override
	public void dispose() {
	}
}
