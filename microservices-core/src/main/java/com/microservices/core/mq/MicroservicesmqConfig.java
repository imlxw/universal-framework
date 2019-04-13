package com.microservices.core.mq;

import com.microservices.config.annotation.PropertyConfig;

@PropertyConfig(prefix = "microservices.mq")
public class MicroservicesmqConfig {
	public static final String TYPE_REDIS = "redis";
	public static final String TYPE_ACTIVEMQ = "activemq";
	public static final String TYPE_ALIYUNMQ = "aliyunmq";
	public static final String TYPE_RABBITMQ = "rabbitmq";
	public static final String TYPE_ZBUS = "zbus";
	public static final String TYPE_QPID = "qpid";

	private String type = TYPE_REDIS;
	private String channel;
	private String syncRecevieMessageChannel; // 可同步接收消息的channel配置

	public String getChannel() {
		return channel;
	}

	public void setChannel(String channel) {
		this.channel = channel;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getSyncRecevieMessageChannel() {
		return syncRecevieMessageChannel;
	}

	public void setSyncRecevieMessageChannel(String syncRecevieMessageChannel) {
		this.syncRecevieMessageChannel = syncRecevieMessageChannel;
	}
}
