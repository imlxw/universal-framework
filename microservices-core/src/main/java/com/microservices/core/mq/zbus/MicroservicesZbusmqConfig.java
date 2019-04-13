package com.microservices.core.mq.zbus;

import com.microservices.config.annotation.PropertyConfig;

@PropertyConfig(prefix = "microservices.mq.zbus")
public class MicroservicesZbusmqConfig {

	private String queue;
	private String broker;

	public String getQueue() {
		return queue;
	}

	public void setQueue(String queue) {
		this.queue = queue;
	}

	public String getBroker() {
		return broker;
	}

	public void setBroker(String broker) {
		this.broker = broker;
	}
}
