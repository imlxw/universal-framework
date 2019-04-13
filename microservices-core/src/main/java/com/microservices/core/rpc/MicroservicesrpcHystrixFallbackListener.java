package com.microservices.core.rpc;

import java.lang.reflect.Method;

import com.microservices.component.hystrix.MicroservicesHystrixCommand;

/**
 * Hystrix 降级监听器
 */
public interface MicroservicesrpcHystrixFallbackListener {

	public Object onFallback(Object proxy, Method method, Object[] args, MicroservicesHystrixCommand command, Throwable exception);

}
