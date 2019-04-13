package com.microservices.core.cache.redis;

import com.microservices.component.redis.MicroservicesRedisConfig;
import com.microservices.config.annotation.PropertyConfig;

/**
 * MicroservicesRedis 缓存的配置文件
 */
@PropertyConfig(prefix = "microservices.cache.redis")
public class MicroservicesRedisCacheConfig extends MicroservicesRedisConfig {

}
