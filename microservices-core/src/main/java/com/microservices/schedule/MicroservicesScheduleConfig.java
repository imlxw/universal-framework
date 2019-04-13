package com.microservices.schedule;

import com.microservices.config.annotation.PropertyConfig;

@PropertyConfig(prefix = "microservices.schedule")
public class MicroservicesScheduleConfig {
	private String cron4jFile = "cron4j.properties";
	private int poolSize = Runtime.getRuntime().availableProcessors() * 8;

	public String getCron4jFile() {
		return cron4jFile;
	}

	public void setCron4jFile(String cron4jFile) {
		this.cron4jFile = cron4jFile;
	}

	public int getPoolSize() {
		return poolSize;
	}

	public void setPoolSize(int poolSize) {
		this.poolSize = poolSize;
	}
}
