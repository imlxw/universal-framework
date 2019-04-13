package com.microservices.core.mq.redismq;

import com.jfinal.log.Log;
import com.microservices.Microservices;
import com.microservices.component.redis.MicroservicesRedis;
import com.microservices.component.redis.MicroservicesRedisManager;
import com.microservices.core.mq.Microservicesmq;
import com.microservices.core.mq.MicroservicesmqBase;
import com.microservices.exception.MicroservicesIllegalConfigException;
import com.microservices.utils.ArrayUtils;

import redis.clients.jedis.BinaryJedisPubSub;

public class MicroservicesRedismqImpl extends MicroservicesmqBase implements Microservicesmq, Runnable {

	private static final Log LOG = Log.getLog(MicroservicesRedismqImpl.class);

	private MicroservicesRedis redis;
	private Thread dequeueThread;

	public MicroservicesRedismqImpl() {
		super();

		MicroservicesmqRedisConfig redisConfig = Microservices.config(MicroservicesmqRedisConfig.class);
		if (redisConfig.isConfigOk()) {
			redis = MicroservicesRedisManager.me().getRedis(redisConfig);
		} else {
			redis = Microservices.me().getRedis();
		}

		if (redis == null) {
			throw new MicroservicesIllegalConfigException("can not use redis mq (redis mq is default), " + "please config microservices.redis.host=yourhost and check your microservices.properties, " + "or use other mq component. ");
		}

		if (ArrayUtils.isNotEmpty(this.channels)) {
			initChannelSubscribe();
		}
	}

	private void initChannelSubscribe() {
		String[] channels = this.channels.toArray(new String[] {});

		redis.subscribe(new BinaryJedisPubSub() {
			@Override
			public void onMessage(byte[] channel, byte[] message) {
				notifyListeners(redis.bytesToKey(channel), Microservices.me().getSerializer().deserialize(message));
			}
		}, redis.keysToBytesArray(channels));

		dequeueThread = new Thread(this);
		dequeueThread.start();
	}

	@Override
	public void enqueue(Object message, String toChannel) {
		redis.lpush(toChannel, message);
	}

	@Override
	public void publish(Object message, String toChannel) {
		redis.publish(redis.keyToBytes(toChannel), Microservices.me().getSerializer().serialize(message));
	}

	@Override
	public void run() {
		for (;;) {
			try {
				doExecuteDequeue();
				Thread.sleep(100);
			} catch (Throwable ex) {
				LOG.error(ex.toString(), ex);
			}
		}
	}

	private void doExecuteDequeue() {
		for (String channel : this.channels) {
			Object data = redis.lpop(channel);
			if (data != null) {
				notifyListeners(channel, data);
			}
		}
	}
}
