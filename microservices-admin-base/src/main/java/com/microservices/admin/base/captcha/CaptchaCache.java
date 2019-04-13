package com.microservices.admin.base.captcha;

import com.jfinal.captcha.Captcha;
import com.jfinal.captcha.ICaptchaCache;
import com.microservices.Microservices;
import com.microservices.admin.base.common.CacheKey;

/**
 * 验证码 redis 集群
 */
public class CaptchaCache implements ICaptchaCache {

	@Override
	public void put(Captcha captcha) {
		Microservices.me().getCache().put(CacheKey.CACHE_CAPTCHAR_SESSION, captcha.getKey(), captcha, (int) (captcha.getExpireAt() - System.currentTimeMillis()) / 1000);
	}

	@Override
	public Captcha get(String key) {
		return Microservices.me().getCache().get(CacheKey.CACHE_CAPTCHAR_SESSION, key);
	}

	@Override
	public void remove(String key) {
		Microservices.me().getCache().remove(CacheKey.CACHE_CAPTCHAR_SESSION, key);
	}

	@Override
	public void removeAll() {
		Microservices.me().getCache().removeAll(CacheKey.CACHE_CAPTCHAR_SESSION);
	}

}
