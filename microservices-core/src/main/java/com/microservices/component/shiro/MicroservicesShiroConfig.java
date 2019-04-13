package com.microservices.component.shiro;

import java.io.File;

import com.jfinal.kit.PathKit;
import com.microservices.config.annotation.PropertyConfig;

@PropertyConfig(prefix = "microservices.shiro")
public class MicroservicesShiroConfig {

	private String loginUrl;
	private String successUrl;
	private String unauthorizedUrl;
	private String shiroIniFile = "shiro.ini";
	private String urlMapping = "/*";

	private String invokeListener;

	public String getLoginUrl() {
		return loginUrl;
	}

	public void setLoginUrl(String loginUrl) {
		this.loginUrl = loginUrl;
	}

	public String getSuccessUrl() {
		return successUrl;
	}

	public void setSuccessUrl(String successUrl) {
		this.successUrl = successUrl;
	}

	public String getUnauthorizedUrl() {
		return unauthorizedUrl;
	}

	public void setUnauthorizedUrl(String unauthorizedUrl) {
		this.unauthorizedUrl = unauthorizedUrl;
	}

	public String getShiroIniFile() {
		return shiroIniFile;
	}

	public void setShiroIniFile(String shiroIniFile) {
		this.shiroIniFile = shiroIniFile;
	}

	public String getUrlMapping() {
		return urlMapping;
	}

	public void setUrlMapping(String urlMapping) {
		this.urlMapping = urlMapping;
	}

	public String getInvokeListener() {
		return invokeListener;
	}

	public void setInvokeListener(String invokeListener) {
		this.invokeListener = invokeListener;
	}

	private Boolean config;

	public boolean isConfigOK() {
		if (config == null) {
			config = new File(PathKit.getRootClassPath(), shiroIniFile).exists();
		}
		return config;
	}
}
