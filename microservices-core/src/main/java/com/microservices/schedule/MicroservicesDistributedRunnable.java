package com.microservices.schedule;

import com.jfinal.log.Log;
import com.microservices.Microservices;
import com.microservices.component.redis.MicroservicesRedis;

/**
 * @version V1.0
 * @Title: 分布式任务
 * @Description: 在分布式应用中，处理分布式应用，基于redis。 特点： 1、简单，无需依赖数据库。 2、高可用，不存在单点故障 3、一致性，在集群环境中，只有一个任务在执行。 4、Failover，支持故障转移
 * @Package com.microservices.schedule
 */
public class MicroservicesDistributedRunnable implements Runnable {

	private static final Log LOG = Log.getLog(MicroservicesDistributedRunnable.class);

	private MicroservicesRedis redis;
	private int expire = 50 * 1000; // 单位毫秒
	private String key;
	private Runnable runnable;

	public MicroservicesDistributedRunnable() {

		this.redis = Microservices.me().getRedis();
		this.key = "microservicesRunnable:" + this.getClass().getName();

		if (redis == null) {
			LOG.warn("redis is null, " + "can not use @EnableDistributedRunnable in your Class[" + this.getClass().getName() + "], " + "or config redis info in microservices.properties");
		}

	}

	public MicroservicesDistributedRunnable(Runnable runnable) {
		this.runnable = runnable;
		this.key = "microservicesRunnable:" + runnable.getClass().getName();
		this.redis = Microservices.me().getRedis();
		if (redis == null) {
			LOG.warn("redis is null, " + "can not use @EnableDistributedRunnable in your Class[" + runnable.getClass().getName() + "], " + "or config redis info in microservices.properties");
		}
	}

	public MicroservicesDistributedRunnable(Runnable runnable, int expire) {
		this.expire = (expire - 1) * 1000;
		this.runnable = runnable;
		this.key = "microservicesRunnable:" + runnable.getClass().getName();
		this.redis = Microservices.me().getRedis();
		if (redis == null) {
			LOG.warn("redis is null, " + "can not use @EnableDistributedRunnable in your Class[" + runnable.getClass().getName() + "], " + "or config redis info in microservices.properties");
		}
	}

	@Override
	public void run() {

		if (redis == null) {
			return;
		}

		Long result = null;

		for (int i = 0; i < 5; i++) {

			Long setTimeMillis = System.currentTimeMillis();
			result = redis.setnx(key, setTimeMillis);

			// error
			if (result == null) {
				quietSleep();
			}

			// setnx fail
			else if (result == 0) {
				Long saveTimeMillis = redis.get(key);
				if (saveTimeMillis == null) {
					reset();
				}
				long ttl = System.currentTimeMillis() - saveTimeMillis;
				if (ttl > expire) {
					// 防止死锁
					reset();
				}

				// 休息 2 秒钟，重新去抢，因为可能别的应用执行失败了
				quietSleep();

			}

			// set success
			else if (result == 1) {
				break;
			}
		}

		// 抢了5次都抢不到，证明已经被别的应用抢走了
		if (result == null || result == 0) {
			return;
		}

		try {

			if (runnable != null) {
				runnable.run();
			} else {
				boolean runSuccess = execute();

				// run()执行失败，让别的分布式应用APP去执行
				// 如果run()执行的时间很长（超过30秒）,那么别的分布式应用可能也抢不到了，只能等待下次轮休
				// 作用：故障转移
				if (!runSuccess) {
					reset();
				}
			}
		}

		// 如果 run() 执行异常，让别的分布式应用APP去执行
		// 作用：故障转移
		catch (Throwable ex) {
			LOG.error(ex.toString(), ex);
			reset();
		}
	}

	/**
	 * 重置分布式的key
	 */
	private void reset() {
		redis.del(key);
	}

	public void quietSleep() {

		int millis = 2000;
		if (this.expire <= 2000) {
			millis = 100;
		} else if (this.expire <= 5000) {
			millis = 500;
		} else if (this.expire <= 300000) {
			millis = 1000;
		}

		try {
			Thread.sleep(millis);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	// for override
	public boolean execute() {
		return true;
	}

}
