package com.microservices.component.hystrix;

import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixThreadPoolKey;

public abstract class MicroservicesHystrixCommand extends HystrixCommand<Object> {

	public MicroservicesHystrixCommand(String key) {
		super(HystrixCommandGroupKey.Factory.asKey(key));
	}

	public MicroservicesHystrixCommand(String key, int executionIsolationThreadTimeoutInMilliseconds) {
		super(HystrixCommandGroupKey.Factory.asKey(key), executionIsolationThreadTimeoutInMilliseconds);
	}

	public MicroservicesHystrixCommand(HystrixCommandGroupKey group) {
		super(group);
	}

	public MicroservicesHystrixCommand(HystrixCommandGroupKey group, HystrixThreadPoolKey threadPool) {
		super(group, threadPool);
	}

	public MicroservicesHystrixCommand(HystrixCommandGroupKey group, int executionIsolationThreadTimeoutInMilliseconds) {
		super(group, executionIsolationThreadTimeoutInMilliseconds);
	}

	public MicroservicesHystrixCommand(HystrixCommandGroupKey group, HystrixThreadPoolKey threadPool, int executionIsolationThreadTimeoutInMilliseconds) {
		super(group, threadPool, executionIsolationThreadTimeoutInMilliseconds);
	}

	public MicroservicesHystrixCommand(Setter setter) {
		super(setter);
	}

}
