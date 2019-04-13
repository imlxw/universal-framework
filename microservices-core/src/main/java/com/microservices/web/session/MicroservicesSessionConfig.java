package com.microservices.web.session;

import com.microservices.Microservices;
import com.microservices.config.annotation.PropertyConfig;
import com.microservices.core.cache.MicroservicesCacheConfig;

@PropertyConfig(prefix = "microservices.web.session")
public class MicroservicesSessionConfig {

	public final static int DEFAULT_MAX_INACTIVE_INTERVAL = 60 * 60;
	public final static String DEFAULT_COOKIE_CONTEXT_PATH = "/";
	public final static int DEFAULT_COOKIE_MAX_AGE = -1;
	public final static String DEFAULT_SESSION_COOKIE_NAME = "_JSID";
	public final static String DEFAULT_SESSION_CACHE_NAME = "MICROSERVICESSESSION";

	private String cookieName = DEFAULT_SESSION_COOKIE_NAME;
	private String cookieDomain;
	private String cookieContextPath = DEFAULT_COOKIE_CONTEXT_PATH;
	private int maxInactiveInterval = DEFAULT_MAX_INACTIVE_INTERVAL;
	private int cookieMaxAge = DEFAULT_COOKIE_MAX_AGE;

	private String cacheName = DEFAULT_SESSION_CACHE_NAME;
	private String cacheType = Microservices.config(MicroservicesCacheConfig.class).getType();

	public String getCookieName() {
		return cookieName;
	}

	public void setCookieName(String cookieName) {
		this.cookieName = cookieName;
	}

	public String getCookieDomain() {
		return cookieDomain;
	}

	public void setCookieDomain(String cookieDomain) {
		this.cookieDomain = cookieDomain;
	}

	public String getCookieContextPath() {
		return cookieContextPath;
	}

	public void setCookieContextPath(String cookieContextPath) {
		this.cookieContextPath = cookieContextPath;
	}

	public int getMaxInactiveInterval() {
		return maxInactiveInterval;
	}

	public void setMaxInactiveInterval(int maxInactiveInterval) {
		this.maxInactiveInterval = maxInactiveInterval;
	}

	public int getCookieMaxAge() {
		return cookieMaxAge;
	}

	public void setCookieMaxAge(int cookieMaxAge) {
		this.cookieMaxAge = cookieMaxAge;
	}

	public String getCacheName() {
		return cacheName;
	}

	public void setCacheName(String cacheName) {
		this.cacheName = cacheName;
	}

	public String getCacheType() {
		return cacheType;
	}

	public void setCacheType(String cacheType) {
		this.cacheType = cacheType;
	}

	private static MicroservicesSessionConfig me;

	public static MicroservicesSessionConfig get() {
		if (me == null) {
			me = Microservices.config(MicroservicesSessionConfig.class);
		}
		return me;
	}
}
