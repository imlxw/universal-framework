package com.microservices.core.cache.ehredis;

import java.io.Serializable;

public class MicroservicesEhredisMessage implements Serializable {

	public static final int ACTION_PUT = 1;
	public static final int ACTION_REMOVE = 2;
	public static final int ACTION_REMOVE_ALL = 3;

	private String clientId;
	private int action;
	private String cacheName;
	private Object key;

	public MicroservicesEhredisMessage() {

	}

	public MicroservicesEhredisMessage(String clientId, int action, String cacheName, Object key) {
		this.clientId = clientId;
		this.action = action;
		this.cacheName = cacheName;
		this.key = key;
	}

	public String getClientId() {
		return clientId;
	}

	public void setClientId(String clientId) {
		this.clientId = clientId;
	}

	public int getAction() {
		return action;
	}

	public void setAction(int action) {
		this.action = action;
	}

	public String getCacheName() {
		return cacheName;
	}

	public void setCacheName(String cacheName) {
		this.cacheName = cacheName;
	}

	public Object getKey() {
		return key;
	}

	public void setKey(Object key) {
		this.key = key;
	}

}
