package com.microservices.core.mq.aliyunmq;

import com.microservices.config.annotation.PropertyConfig;

@PropertyConfig(prefix = "microservices.mq.aliyun")
public class MicroservicesAliyunmqConfig {

	private String accessKey;
	private String secretKey;
	private String producerId;
	private String addr;
	private String sendMsgTimeoutMillis = "3000";

	public String getAccessKey() {
		return accessKey;
	}

	public void setAccessKey(String accessKey) {
		this.accessKey = accessKey;
	}

	public String getSecretKey() {
		return secretKey;
	}

	public void setSecretKey(String secretKey) {
		this.secretKey = secretKey;
	}

	public String getProducerId() {
		return producerId;
	}

	public void setProducerId(String producerId) {
		this.producerId = producerId;
	}

	public String getAddr() {
		return addr;
	}

	public void setAddr(String addr) {
		this.addr = addr;
	}

	public String getSendMsgTimeoutMillis() {
		return sendMsgTimeoutMillis;
	}

	public void setSendMsgTimeoutMillis(String sendMsgTimeoutMillis) {
		this.sendMsgTimeoutMillis = sendMsgTimeoutMillis;
	}

}
