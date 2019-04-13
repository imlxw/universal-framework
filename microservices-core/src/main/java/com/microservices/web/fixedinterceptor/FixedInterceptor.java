package com.microservices.web.fixedinterceptor;

/**
 * @title 不会被 @Clear 清除掉的 拦截器
 * @version V1.0
 * @Package com.microservices.web.handler
 */
public interface FixedInterceptor {
	void intercept(FixedInvocation inv);
}
