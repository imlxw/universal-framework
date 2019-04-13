package com.microservices.core.rpc;

import java.lang.reflect.Method;

import com.microservices.component.hystrix.MicroservicesHystrixCommand;
import com.netflix.hystrix.exception.HystrixTimeoutException;

public class MicroservicesrpcHystrixFallbackListenerDefault implements MicroservicesrpcHystrixFallbackListener {

	@Override
	public Object onFallback(Object proxy, Method method, Object[] args, MicroservicesHystrixCommand command, Throwable exception) {
		if (exception instanceof HystrixTimeoutException) {
			System.err.println(
					"rpc request timeout, the defalut timeout value is 5000 milliseconds, " + "you can config microservices.rpc.hystrixTimeout to set the value, " + "or config \"microservices.rpc.hystrixEnable = false\" to close hystrix.");
		}
		exception.printStackTrace();
		return null;
	}

}
