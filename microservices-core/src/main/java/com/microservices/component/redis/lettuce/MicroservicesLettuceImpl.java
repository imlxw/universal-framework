package com.microservices.component.redis.lettuce;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

import com.microservices.component.redis.MicroservicesRedis;
import com.microservices.component.redis.MicroservicesRedisConfig;

import io.lettuce.core.RedisClient;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.support.ConnectionPoolSupport;
import redis.clients.jedis.BinaryJedisPubSub;
import redis.clients.jedis.JedisPubSub;

/**
 * @version V1.0
 */
public class MicroservicesLettuceImpl implements MicroservicesRedis {

	protected MicroservicesRedisConfig config;
	protected RedisClient redisClient;
	protected GenericObjectPool<StatefulRedisConnection<Object, Object>> pool;

	public MicroservicesLettuceImpl(MicroservicesRedisConfig config) {
		this.config = config;
		this.redisClient = RedisClient.create();
		this.pool = ConnectionPoolSupport.createGenericObjectPool(() -> redisClient.connect(new MicroservicesLettuceCodec()), new GenericObjectPoolConfig());

	}

	@Override
	public String set(Object key, Object value) {
		StatefulRedisConnection<Object, Object> connection = null;
		try {
			connection = pool.borrowObject();
			return connection.sync().set(key, value);
		} catch (Exception e) {
			throw new LettuceException(e);
		} finally {
			if (connection != null) {
				connection.close();
			}
		}
	}

	@Override
	public Long setnx(Object key, Object value) {
		return null;
	}

	@Override
	public String setWithoutSerialize(Object key, Object value) {
		return null;
	}

	@Override
	public String setex(Object key, int seconds, Object value) {
		return null;
	}

	@Override
	public <T> T get(Object key) {
		return null;
	}

	@Override
	public String getWithoutSerialize(Object key) {
		return null;
	}

	@Override
	public Long del(Object key) {
		return null;
	}

	@Override
	public Long del(Object... keys) {
		return null;
	}

	@Override
	public Set<String> keys(String pattern) {
		return null;
	}

	@Override
	public String mset(Object... keysValues) {
		return null;
	}

	@Override
	public List mget(Object... keys) {
		return null;
	}

	@Override
	public Long decr(Object key) {
		return null;
	}

	@Override
	public Long decrBy(Object key, long value) {
		return null;
	}

	@Override
	public Long incr(Object key) {
		return null;
	}

	@Override
	public Long incrBy(Object key, long value) {
		return null;
	}

	@Override
	public boolean exists(Object key) {
		return false;
	}

	@Override
	public String randomKey() {
		return null;
	}

	@Override
	public String rename(Object oldkey, Object newkey) {
		return null;
	}

	@Override
	public Long move(Object key, int dbIndex) {
		return null;
	}

	@Override
	public String migrate(String host, int port, Object key, int destinationDb, int timeout) {
		return null;
	}

	@Override
	public String select(int databaseIndex) {
		return null;
	}

	@Override
	public Long expire(Object key, int seconds) {
		return null;
	}

	@Override
	public Long expireAt(Object key, long unixTime) {
		return null;
	}

	@Override
	public Long pexpire(Object key, long milliseconds) {
		return null;
	}

	@Override
	public Long pexpireAt(Object key, long millisecondsTimestamp) {
		return null;
	}

	@Override
	public <T> T getSet(Object key, Object value) {
		return null;
	}

	@Override
	public Long persist(Object key) {
		return null;
	}

	@Override
	public String type(Object key) {
		return null;
	}

	@Override
	public Long ttl(Object key) {
		return null;
	}

	@Override
	public Long pttl(Object key) {
		return null;
	}

	@Override
	public Long objectRefcount(Object key) {
		return null;
	}

	@Override
	public Long objectIdletime(Object key) {
		return null;
	}

	@Override
	public Long hset(Object key, Object field, Object value) {
		return null;
	}

	@Override
	public String hmset(Object key, Map<Object, Object> hash) {
		return null;
	}

	@Override
	public <T> T hget(Object key, Object field) {
		return null;
	}

	@Override
	public List hmget(Object key, Object... fields) {
		return null;
	}

	@Override
	public Long hdel(Object key, Object... fields) {
		return null;
	}

	@Override
	public boolean hexists(Object key, Object field) {
		return false;
	}

	@Override
	public Map hgetAll(Object key) {
		return null;
	}

	@Override
	public List hvals(Object key) {
		return null;
	}

	@Override
	public Set<Object> hkeys(Object key) {
		return null;
	}

	@Override
	public Long hlen(Object key) {
		return null;
	}

	@Override
	public Long hincrBy(Object key, Object field, long value) {
		return null;
	}

	@Override
	public Double hincrByFloat(Object key, Object field, double value) {
		return null;
	}

	@Override
	public <T> T lindex(Object key, long index) {
		return null;
	}

	@Override
	public Long getCounter(Object key) {
		return null;
	}

	@Override
	public Long llen(Object key) {
		return null;
	}

	@Override
	public <T> T lpop(Object key) {
		return null;
	}

	@Override
	public Long lpush(Object key, Object... values) {
		return null;
	}

	@Override
	public String lset(Object key, long index, Object value) {
		return null;
	}

	@Override
	public Long lrem(Object key, long count, Object value) {
		return null;
	}

	@Override
	public List lrange(Object key, long start, long end) {
		return null;
	}

	@Override
	public String ltrim(Object key, long start, long end) {
		return null;
	}

	@Override
	public <T> T rpop(Object key) {
		return null;
	}

	@Override
	public <T> T rpoplpush(Object srcKey, Object dstKey) {
		return null;
	}

	@Override
	public Long rpush(Object key, Object... values) {
		return null;
	}

	@Override
	public List blpop(Object... keys) {
		return null;
	}

	@Override
	public List blpop(Integer timeout, Object... keys) {
		return null;
	}

	@Override
	public List brpop(Object... keys) {
		return null;
	}

	@Override
	public List brpop(Integer timeout, Object... keys) {
		return null;
	}

	@Override
	public String ping() {
		return null;
	}

	@Override
	public Long sadd(Object key, Object... members) {
		return null;
	}

	@Override
	public Long scard(Object key) {
		return null;
	}

	@Override
	public <T> T spop(Object key) {
		return null;
	}

	@Override
	public Set smembers(Object key) {
		return null;
	}

	@Override
	public boolean sismember(Object key, Object member) {
		return false;
	}

	@Override
	public Set sinter(Object... keys) {
		return null;
	}

	@Override
	public <T> T srandmember(Object key) {
		return null;
	}

	@Override
	public List srandmember(Object key, int count) {
		return null;
	}

	@Override
	public Long srem(Object key, Object... members) {
		return null;
	}

	@Override
	public Set sunion(Object... keys) {
		return null;
	}

	@Override
	public Set sdiff(Object... keys) {
		return null;
	}

	@Override
	public Long zadd(Object key, double score, Object member) {
		return null;
	}

	@Override
	public Long zadd(Object key, Map<Object, Double> scoreMembers) {
		return null;
	}

	@Override
	public Long zcard(Object key) {
		return null;
	}

	@Override
	public Long zcount(Object key, double min, double max) {
		return null;
	}

	@Override
	public Double zincrby(Object key, double score, Object member) {
		return null;
	}

	@Override
	public Set zrange(Object key, long start, long end) {
		return null;
	}

	@Override
	public Set zrevrange(Object key, long start, long end) {
		return null;
	}

	@Override
	public Set zrangeByScore(Object key, double min, double max) {
		return null;
	}

	@Override
	public Long zrank(Object key, Object member) {
		return null;
	}

	@Override
	public Long zrevrank(Object key, Object member) {
		return null;
	}

	@Override
	public Long zrem(Object key, Object... members) {
		return null;
	}

	@Override
	public Double zscore(Object key, Object member) {
		return null;
	}

	@Override
	public void publish(String channel, String message) {

	}

	@Override
	public void publish(byte[] channel, byte[] message) {

	}

	@Override
	public void subscribe(JedisPubSub listener, String... channels) {

	}

	@Override
	public void subscribe(BinaryJedisPubSub binaryListener, byte[]... channels) {

	}

	@Override
	public byte[] keyToBytes(Object key) {
		return new byte[0];
	}

	@Override
	public String bytesToKey(byte[] bytes) {
		return null;
	}

	@Override
	public byte[][] keysToBytesArray(Object... keys) {
		return new byte[0][];
	}

	@Override
	public void fieldSetFromBytesSet(Set<byte[]> data, Set<Object> result) {

	}

	@Override
	public byte[] valueToBytes(Object value) {
		return new byte[0];
	}

	@Override
	public Object valueFromBytes(byte[] bytes) {
		return null;
	}

	@Override
	public byte[][] valuesToBytesArray(Object... valuesArray) {
		return new byte[0][];
	}

	@Override
	public void valueSetFromBytesSet(Set<byte[]> data, Set<Object> result) {

	}

	@Override
	public List valueListFromBytesList(Collection<byte[]> data) {
		return null;
	}
}
