package com.microservices.core.rpc.dubbo;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import com.alibaba.dubbo.common.URL;
import com.alibaba.dubbo.rpc.Invoker;
import com.alibaba.dubbo.rpc.RpcException;
import com.alibaba.dubbo.rpc.proxy.AbstractProxyFactory;
import com.alibaba.dubbo.rpc.proxy.AbstractProxyInvoker;
import com.alibaba.dubbo.rpc.proxy.InvokerInvocationHandler;
import com.microservices.Microservices;
import com.microservices.component.hystrix.MicroservicesHystrixCommand;
import com.microservices.component.opentracing.MicroservicesSpanContext;
import com.microservices.core.rpc.MicroservicesrpcConfig;
import com.microservices.core.rpc.MicroservicesrpcManager;
import com.microservices.utils.StringUtils;

import io.opentracing.Span;

/**
 * 扩展 dubbo 的代理类 用于 Hystrix 的控制和统计
 */
public class MicroservicesDubboProxyFactory extends AbstractProxyFactory {

	static MicroservicesrpcConfig rpcConfig = Microservices.config(MicroservicesrpcConfig.class);

	@Override
	public <T> T getProxy(Invoker<T> invoker, Class<?>[] interfaces) {
		return (T) Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(), interfaces, new MicroservicesInvocationHandler(invoker));
	}

	@Override
	public <T> Invoker<T> getInvoker(T proxy, Class<T> type, URL url) throws RpcException {
		return new AbstractProxyInvoker<T>(proxy, type, url) {
			@Override
			protected Object doInvoke(T proxy, String methodName, Class<?>[] parameterTypes, Object[] arguments) throws Throwable {
				Method method = proxy.getClass().getMethod(methodName, parameterTypes);
				return method.invoke(proxy, arguments);
			}
		};
	}

	/**
	 * InvocationHandler 的代理类，InvocationHandler在motan内部创建 MicroservicesInvocationHandler代理后，可以对某个方法执行之前做些额外的操作：例如通过 Hystrix 包装
	 */
	public static class MicroservicesInvocationHandler extends InvokerInvocationHandler {

		public MicroservicesInvocationHandler(Invoker<?> handler) {
			super(handler);
		}

		@Override
		public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

			if (!rpcConfig.isHystrixEnable()) {
				return super.invoke(proxy, method, args);
			}

			/**
			 * 过滤系统方法，不走hystrix
			 */
			if ("hashCode".equals(method.getName()) || "toString".equals(method.getName()) || "equals".equals(method.getName()) || "getClass".equals(method.getName())) {

				return super.invoke(proxy, method, args);

			}

			String key = rpcConfig.getHystrixKeyByMethod(method.getName());
			if (StringUtils.isBlank(key) && rpcConfig.isHystrixAutoConfig()) {
				key = method.getDeclaringClass().getName() + "." + method.getName();
			}

			final Span span = MicroservicesDubboTracingFilterKits.getActiveSpan();

			return StringUtils.isBlank(key) ? super.invoke(proxy, method, args) : Microservices.hystrix(new MicroservicesHystrixCommand(key, rpcConfig.getHystrixTimeout()) {
				@Override
				public Object run() throws Exception {
					try {
						MicroservicesSpanContext.add(span);
						return MicroservicesInvocationHandler.super.invoke(proxy, method, args);
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
