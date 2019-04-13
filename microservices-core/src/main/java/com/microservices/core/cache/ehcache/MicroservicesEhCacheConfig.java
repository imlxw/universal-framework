package com.microservices.core.cache.ehcache;

import com.microservices.config.annotation.PropertyConfig;

@PropertyConfig(prefix = "microservices.cache.ehcache")
public class MicroservicesEhCacheConfig {

	private String configFileName;

	public String getConfigFileName() {
		return configFileName;
	}

	public void setConfigFileName(String configFileName) {
		this.configFileName = configFileName;
	}
}
