package com.microservices.config;

import com.microservices.config.annotation.PropertyConfig;

/**
 * Microservices配置
 */
@PropertyConfig(prefix = "microservices.config")
public class MicroservicesConfigConfig {

	/**
	 * 是否启用远程配置
	 */
	private boolean remoteEnable = false;

	/**
	 * 远程配置的网址
	 */
	private String remoteUrl;

	/**
	 * 是否把本应用配置为远程配置的服务器
	 */
	private boolean serverEnable = false;

	/**
	 * 给远程提供的配置文件的路径，多个用分号（；）隔开
	 */
	private String path;

	/**
	 * 要排除的配置文件
	 */
	private String exclude;

	/**
	 * 应用名 区分配置文件
	 */
	private String appName = "microservices";

	public String getAppName() {
		return appName;
	}

	public void setAppName(String appName) {
		this.appName = appName;
	}

	public boolean isRemoteEnable() {
		return remoteEnable;
	}

	public void setRemoteEnable(boolean remoteEnable) {
		this.remoteEnable = remoteEnable;
	}

	public String getRemoteUrl() {
		return remoteUrl;
	}

	public void setRemoteUrl(String remoteUrl) {
		this.remoteUrl = remoteUrl;
	}

	public boolean isServerEnable() {
		return serverEnable;
	}

	public void setServerEnable(boolean serverEnable) {
		this.serverEnable = serverEnable;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getExclude() {
		return exclude;
	}

	public void setExclude(String exclude) {
		this.exclude = exclude;
	}
}
