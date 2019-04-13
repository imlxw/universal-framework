package com.microservices.wechat;

import com.jfinal.weixin.sdk.cache.IAccessTokenCache;
import com.microservices.Microservices;

public class MicroservicesAccessTokenCache implements IAccessTokenCache {

	static final String cache_name = "__microservices_wechat_access_tokens";

	@Override
	public String get(String key) {
		return Microservices.me().getCache().get(cache_name, key);
	}

	@Override
	public void set(String key, String value) {
		Microservices.me().getCache().put(cache_name, key, value);
	}

	@Override
	public void remove(String key) {
		Microservices.me().getCache().remove(cache_name, key);
	}
}
