package com.microservices.web.session;

import java.util.Collections;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.microservices.core.cache.MicroservicesCache;
import com.microservices.core.cache.MicroservicesCacheManager;

public class MicroservicesServletRequestWrapper extends HttpServletRequestWrapper {

	private HttpServletResponse response;
	private MicroservicesHttpSession httpSession;
	private MicroservicesCache microservicesCache;

	private int maxInactiveInterval = MicroservicesSessionConfig.get().getMaxInactiveInterval();
	private String cookieName = MicroservicesSessionConfig.get().getCookieName();
	private String cookiePath = MicroservicesSessionConfig.get().getCookieContextPath();
	private String cookieDomain = MicroservicesSessionConfig.get().getCookieDomain();
	private int cookieMaxAge = MicroservicesSessionConfig.get().getCookieMaxAge();
	private String cacheName = MicroservicesSessionConfig.get().getCacheName();
	private String cacheType = MicroservicesSessionConfig.get().getCacheType();

	public MicroservicesServletRequestWrapper(HttpServletRequest request, HttpServletResponse response) {
		super(request);
		this.response = response;
		this.microservicesCache = MicroservicesCacheManager.me().getCache(cacheType);
	}

	@Override
	public HttpSession getSession() {
		return getSession(true);
	}

	@Override
	public HttpSession getSession(boolean create) {
		if (httpSession != null) {
			return httpSession;
		}

		String sessionId = getCookie(cookieName);
		if (sessionId != null) {
			httpSession = new MicroservicesHttpSession(sessionId, getRequest().getServletContext(), createSessionStore(sessionId));
			httpSession.setMaxInactiveInterval(maxInactiveInterval);
		} else if (create) {
			sessionId = UUID.randomUUID().toString().replace("-", "");
			httpSession = new MicroservicesHttpSession(sessionId, getRequest().getServletContext(), createSessionStore(sessionId));
			httpSession.setMaxInactiveInterval(maxInactiveInterval);
			setCookie(cookieName, sessionId, cookieMaxAge);
		}
		return httpSession;
	}

	private Map<String, Object> createSessionStore(String sessionId) {
		Map<String, Object> store = microservicesCache.get(cacheName, sessionId);
		if (store == null) {
			store = Collections.emptyMap();
		}
		return store;
	}

	/**
	 * http请求技术时，更新session信息，包括：刷新session的存储时间，更新session数据，清空session数据等
	 */
	public void refreshSession() {
		if (httpSession == null) {
			return;
		}

		// session已经被整体删除，调用了session.invalidate()
		if (!httpSession.isValid()) {
			microservicesCache.remove(cacheName, httpSession.getId());
			setCookie(cookieName, null, 0);
		}

		// 空的httpSession数据
		else if (httpSession.isEmpty()) {
			microservicesCache.remove(cacheName, httpSession.getId());
			setCookie(cookieName, null, 0);
		}

		// session 已经被修改(session数据的增删改查)
		else if (httpSession.isDataChanged()) {
			Map<String, Object> snapshot = httpSession.snapshot();

			// 数据已经全部被删除了
			if (snapshot.isEmpty()) {
				microservicesCache.remove(cacheName, httpSession.getId());
				setCookie(cookieName, null, 0);
			} else {
				microservicesCache.put(cacheName, httpSession.getId(), snapshot, maxInactiveInterval);
			}
		}
		// 更新session存储时间
		else {
			microservicesCache.setTtl(cacheName, httpSession.getId(), maxInactiveInterval);
		}
	}

	/**
	 * Get cookie value by cookie name.
	 */
	private String getCookie(String name) {
		Cookie cookie = getCookieObject(name);
		return cookie != null ? cookie.getValue() : null;
	}

	/**
	 * Get cookie object by cookie name.
	 */
	private Cookie getCookieObject(String name) {
		Cookie[] cookies = ((HttpServletRequest) getRequest()).getCookies();
		if (cookies != null)
			for (Cookie cookie : cookies)
				if (cookie.getName().equals(name))
					return cookie;
		return null;
	}

	/**
	 * @param name
	 * @param value
	 * @param maxAgeInSeconds
	 */
	private void setCookie(String name, String value, int maxAgeInSeconds) {
		Cookie cookie = new Cookie(name, value);
		cookie.setMaxAge(maxAgeInSeconds);
		cookie.setPath(cookiePath);
		if (cookieDomain != null) {
			cookie.setDomain(cookieDomain);
		}
		cookie.setHttpOnly(true);
		response.addCookie(cookie);
	}

}
