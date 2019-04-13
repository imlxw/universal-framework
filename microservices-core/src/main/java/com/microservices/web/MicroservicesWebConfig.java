package com.microservices.web;

import com.microservices.config.annotation.PropertyConfig;

/**
 * @version V1.0
 * @Package com.microservices.web
 */
@PropertyConfig(prefix = "microservices.web")
public class MicroservicesWebConfig {

	public static final String ACTION_CACHE_KEYGENERATOR_TYPE_DEFAULT = "default";

	private boolean actionCacheEnable = true;
	private String actionCacheKeyGeneratorType = ACTION_CACHE_KEYGENERATOR_TYPE_DEFAULT;

	// websocket 的相关配置
	// 具体使用请参考：https://github.com/undertow-io/undertow/tree/master/examples/src/main/java/io/undertow/examples/jsrwebsockets
	private boolean websocketEnable = false;
	private String websocketBasePath;
	private int websocketBufferPoolSize = 100;

	public static final String DEFAULT_COOKIE_ENCRYPT_KEY = "MICROSERVICES_DEFAULT_ENCRYPT_KEY";
	private String cookieEncryptKey = DEFAULT_COOKIE_ENCRYPT_KEY;

	public boolean isActionCacheEnable() {
		return actionCacheEnable;
	}

	public void setActionCacheEnable(boolean actionCacheEnable) {
		this.actionCacheEnable = actionCacheEnable;
	}

	public String getActionCacheKeyGeneratorType() {
		return actionCacheKeyGeneratorType;
	}

	public void setActionCacheKeyGeneratorType(String actionCacheKeyGeneratorType) {
		this.actionCacheKeyGeneratorType = actionCacheKeyGeneratorType;
	}

	public boolean isWebsocketEnable() {
		return websocketEnable;
	}

	public void setWebsocketEnable(boolean websocketEnable) {
		this.websocketEnable = websocketEnable;
	}

	public int getWebsocketBufferPoolSize() {
		return websocketBufferPoolSize;
	}

	public void setWebsocketBufferPoolSize(int websocketBufferPoolSize) {
		this.websocketBufferPoolSize = websocketBufferPoolSize;
	}

	public String getCookieEncryptKey() {
		return cookieEncryptKey;
	}

	public void setCookieEncryptKey(String cookieEncryptKey) {
		this.cookieEncryptKey = cookieEncryptKey;
	}

	public String getWebsocketBasePath() {
		return websocketBasePath;
	}

	public void setWebsocketBasePath(String websocketBasePath) {
		this.websocketBasePath = websocketBasePath;
	}

	@Override
	public String toString() {
		return "MicroservicesWebConfig {" + "actionCacheEnable=" + actionCacheEnable + ", actionCacheKeyGeneratorType='" + actionCacheKeyGeneratorType + '\'' + ", websocketEnable=" + websocketEnable + ", websocketBufferPoolSize="
				+ websocketBufferPoolSize + ", cookieEncryptKey='" + cookieEncryptKey + '\'' + '}';
	}
}
