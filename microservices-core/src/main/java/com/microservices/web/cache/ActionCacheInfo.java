package com.microservices.web.cache;

import java.io.Serializable;

/**
 * @version V1.0
 * @Package com.microservices.web.cache
 */
public class ActionCacheInfo implements Serializable {

	private String key;
	private String group;
	private int liveSeconds;

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getGroup() {
		return group;
	}

	public void setGroup(String group) {
		this.group = group;
	}

	public int getLiveSeconds() {
		return liveSeconds;
	}

	public void setLiveSeconds(int liveSeconds) {
		this.liveSeconds = liveSeconds;
	}
}
