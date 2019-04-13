package com.microservices.event;

import java.io.Serializable;

public class MicroservicesEvent implements Serializable {

	private static final long serialVersionUID = 1L;

	private final long timestamp;
	private String action;
	private Object data;

	public MicroservicesEvent(String action, Object data) {
		this.action = action;
		this.data = data;
		this.timestamp = System.currentTimeMillis();
	}

	@SuppressWarnings("unchecked")
	public <M> M getData() {
		return (M) data;
	}

	public String getAction() {
		return action;
	}

	public long getTimestamp() {
		return this.timestamp;
	}

	@Override
	public String toString() {
		return "MicroservicesEvent [timestamp=" + timestamp + ", action=" + action + ", data=" + data + "]";
	}

}
