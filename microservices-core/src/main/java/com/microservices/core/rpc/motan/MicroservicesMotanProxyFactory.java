package com.microservices.core.rpc.motan;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import com.microservices.Microservices;
import com.microservices.component.hystrix.MicroservicesHystrixCommand;
import com.microservices.component.opentracing.MicroservicesSpanContext;
import com.microservices.core.rpc.MicroservicesrpcConfig;
import com.microservices.core.rpc.MicroservicesrpcManager;
import com.microservices.utils.StringUtils;
import com.weibo.api.motan.core.extension.SpiMeta;
import com.weibo.api.motan.proxy.ProxyFactory;

import io.opentracing.Span;

/**
 * 扩展 motan 的代理类 用于 Hystrix 的控制和统计
 */
@SpiMeta(name = "microservices")
public class MicroservicesMotanProxyFactory implements ProxyFactory {

	static MicroservicesrpcConfig rpcConfig = Microservices.config(MicroservicesrpcConfig.class);

	@Override
	public <T> T getProxy(Class<T> clz, InvocationHandler invocationHandler) {
		// 默认是 jdkProxy
		// return (T) Proxy.newProxyInstance(this.getClass().getClassLoader(), new Class[]{clz}, invocationHandler);
		return (T) Proxy.newProxyInstance(this.getClass().getClassLoader(), new Class[] { clz }, new MicroservicesInvocationHandler(invocationHandler));
	}

	/**
	 * InvocationHandler 的代理类，InvocationHandler在motan内部创建 MicroservicesInvocationHandler代理后，可以对某个方法执行之前做些额外的操作：例如通过 Hystrix 包装
	 */
	public static class MicroservicesInvocationHandler implements InvocationHandler {
		private final InvocationHandler handler;

		public MicroservicesInvocationHandler(InvocationHandler handler) {
			this.handler = handler;
		}

		@Override
		public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

			if (!rpcConfig.isHystrixEnable()) {
				return handler.invoke(proxy, method, args);
			}

			/**
			 * 过滤系统方法，不走hystrix
			 */
			if ("hashCode".equals(method.getName()) || "toString".equals(method.getName()) || "equals".equals(method.getName()) || "getClass".equals(method.getName())) {

				return handler.invoke(proxy, method, args);
			}

			final Span span = MicroservicesMotanTracingFilter.getActiveSpan();

			String key = rpcConfig.getHystrixKeyByMethod(method.getName());
			if (StringUtils.isBlank(key) && rpcConfig.isHystrixAutoConfig()) {
				key = method.getDeclaringClass().getName() + "." + method.getName();
			}

			return StringUtils.isBlank(key) ? handler.invoke(proxy, method, args) : Microservices.hystrix(new MicroservicesHystrixCommand(key, rpcConfig.getHystrixTimeout()) {

				@Override
				public Object run() throws Exception {
					try {
						MicroservicesSpanContext.add(span);

						return handler.invoke(proxy, method, args);
					} catch (Throwable throwable) {
						throw (Exception) throwable;
					} finally {
						MicroservicesSpanContext.release();
					}

				}

				@Override
				public Object getFallback() {
					return MicroservicesrpcManager.me().getHystrixFallbackListener().onFallback(proxy, method, args, this, this.getExecutionException());
				}
			});

		}
	}
}
