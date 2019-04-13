package com.microservices.core.mq.redismq;

import com.microservices.component.redis.MicroservicesRedisConfig;
import com.microservices.config.annotation.PropertyConfig;

@PropertyConfig(prefix = "microservices.mq.redis")
public class MicroservicesmqRedisConfig extends MicroservicesRedisConfig {

}
