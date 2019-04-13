package com.microservices.core.mq;

public interface MicroservicesmqMessageListener {

	/**
	 * @param channel of topic
	 * @param message topic message
	 */
	void onMessage(String channel, Object message);
}
