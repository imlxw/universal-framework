package com.microservices.aop.jfinal;

import com.jfinal.config.Handlers;
import com.jfinal.handler.Handler;
import com.microservices.Microservices;

/**
 * Jfinal Handlers 的代理类，方便为Handler插件的自动注入功能
 */
public class JfinalHandlers {

	private final Handlers handlers;

	public JfinalHandlers(Handlers handlers) {
		this.handlers = handlers;
	}

	public JfinalHandlers add(Handler handler) {
		Microservices.injectMembers(handler);
		handlers.add(handler);
		return this;
	}

	public JfinalHandlers add(int index, Handler handler) {
		Microservices.injectMembers(handler);
		handlers.getHandlerList().add(index, handler);
		return this;
	}
}
