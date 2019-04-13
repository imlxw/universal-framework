package com.microservices.core.cache;

import com.microservices.config.annotation.PropertyConfig;

@PropertyConfig(prefix = "microservices.cache")
public class MicroservicesCacheConfig {

	public static final String TYPE_EHCACHE = "ehcache";
	public static final String TYPE_REDIS = "redis";
	public static final String TYPE_EHREDIS = "ehredis";
	public static final String TYPE_J2CACHE = "j2cache";

	private String type = TYPE_EHCACHE;

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
}
