package com.microservices.web.cache;

public class ActionCacheContext {

	private static ThreadLocal<ActionCacheInfo> threadLocal = new ThreadLocal<>();

	public static void hold(ActionCacheInfo cacheName) {
		threadLocal.set(cacheName);
	}

	public static ActionCacheInfo get() {
		return threadLocal.get();
	}

	public static void release() {
		threadLocal.remove();
	}

}
