package com.microservices.wechat;

import com.jfinal.weixin.sdk.api.ApiConfig;
import com.microservices.config.annotation.PropertyConfig;
import com.microservices.utils.StringUtils;

@PropertyConfig(prefix = "microservices.wechat")
public class MicroservicesWechatConfig {

	private String debug = "false";
	private String appId;
	private String appSecret;
	private String token;
	private String partner;
	private String paternerKey;
	private String cert;

	public String getDebug() {
		return debug;
	}

	public void setDebug(String debug) {
		this.debug = debug;
	}

	public boolean isDebug() {
		return "true".equalsIgnoreCase(debug);
	}

	public String getAppId() {
		return appId;
	}

	public void setAppId(String appId) {
		this.appId = appId;
	}

	public String getAppSecret() {
		return appSecret;
	}

	public void setAppSecret(String appSecret) {
		this.appSecret = appSecret;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public String getPartner() {
		return partner;
	}

	public void setPartner(String partner) {
		this.partner = partner;
	}

	public String getPaternerKey() {
		return paternerKey;
	}

	public void setPaternerKey(String paternerKey) {
		this.paternerKey = paternerKey;
	}

	public String getCert() {
		return cert;
	}

	public void setCert(String cert) {
		this.cert = cert;
	}

	public boolean isConfigOk() {
		return StringUtils.isNotBlank(appId) && StringUtils.isNotBlank(appSecret) && StringUtils.isNotBlank(token);
	}

	public ApiConfig getApiConfig() {

		if (!isConfigOk()) {
			return null;
		}

		ApiConfig config = new ApiConfig();
		config.setAppId(appId);
		config.setAppSecret(appSecret);
		config.setToken(token);
		return config;
	}
}
