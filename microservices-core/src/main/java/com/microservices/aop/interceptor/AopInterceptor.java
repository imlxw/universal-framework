package com.microservices.aop.interceptor;

import java.lang.reflect.Method;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

import com.jfinal.aop.Interceptor;
import com.jfinal.aop.InterceptorManager;
import com.jfinal.core.Controller;
import com.microservices.Microservices;
import com.microservices.aop.interceptor.cache.MicroservicesCacheEvictInterceptor;
import com.microservices.aop.interceptor.cache.MicroservicesCacheInterceptor;
import com.microservices.aop.interceptor.cache.MicroservicesCachePutInterceptor;
import com.microservices.aop.interceptor.cache.MicroservicesCachesEvictInterceptor;
import com.microservices.aop.interceptor.metric.MicroservicesMetricConcurrencyAopInterceptor;
import com.microservices.aop.interceptor.metric.MicroservicesMetricCounterAopInterceptor;
import com.microservices.aop.interceptor.metric.MicroservicesMetricHistogramAopInterceptor;
import com.microservices.aop.interceptor.metric.MicroservicesMetricMeterAopInterceptor;
import com.microservices.aop.interceptor.metric.MicroservicesMetricTimerAopInterceptor;
import com.microservices.component.hystrix.MicroservicesHystrixCommand;
import com.microservices.component.hystrix.annotation.EnableHystrixCommand;
import com.microservices.utils.ClassKits;
import com.microservices.utils.StringUtils;

/**
 * 用户Hystrix命令调用，方便Hystrix控制
 */
public class AopInterceptor implements MethodInterceptor {

	@Override
	public Object invoke(MethodInvocation methodInvocation) throws Throwable {

		EnableHystrixCommand enableHystrixCommand = methodInvocation.getMethod().getAnnotation(EnableHystrixCommand.class);

		if (enableHystrixCommand == null) {
			return doJFinalAOPInvoke(methodInvocation);
		}

		Class targetClass = ClassKits.getUsefulClass(methodInvocation.getThis().getClass());
		String faillMethod = enableHystrixCommand.failMethod();
		String commandKey = enableHystrixCommand.key();

		if (StringUtils.isBlank(commandKey)) {
			commandKey = methodInvocation.getMethod().getName();
		}

		return Microservices.hystrix(new MicroservicesHystrixCommand(commandKey) {
			@Override
			public Object run() throws Exception {
				try {
					return doJFinalAOPInvoke(methodInvocation);
				} catch (Throwable throwable) {
					throw (Exception) throwable;
				}
			}

			@Override
			protected Object getFallback() {
				if (StringUtils.isBlank(faillMethod)) {
					getExecutionException().printStackTrace();
					return null;
				}

				Method method = null;
				try {
					method = targetClass.getMethod(faillMethod, MicroservicesHystrixCommand.class);
				} catch (NoSuchMethodException ex) {
				}

				if (method != null) {
					try {
						method.setAccessible(true);
						return method.invoke(methodInvocation.getThis(), this);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}

				try {
					method = targetClass.getMethod(faillMethod);
				} catch (NoSuchMethodException e) {
					e.printStackTrace();
				}

				if (method != null) {
					try {
						method.setAccessible(true);
						return method.invoke(methodInvocation.getThis());
					} catch (Exception e) {
						e.printStackTrace();
					}
				}

				return super.getFallback();
			}
		});
	}

	public static final Interceptor[] INTERS = { new MicroservicesMetricCounterAopInterceptor(), new MicroservicesMetricConcurrencyAopInterceptor(), new MicroservicesMetricMeterAopInterceptor(), new MicroservicesMetricTimerAopInterceptor(),
			new MicroservicesMetricHistogramAopInterceptor(), new MicroservicesCacheEvictInterceptor(), new MicroservicesCachesEvictInterceptor(), new MicroservicesCachePutInterceptor(), new MicroservicesCacheInterceptor() };

	private Object doJFinalAOPInvoke(MethodInvocation methodInvocation) throws Throwable {

		Class targetClass = methodInvocation.getThis().getClass();

		// 过滤掉controller，因为controller由action去执行@Before相关注解了
		if (Controller.class.isAssignableFrom(targetClass)) {
			return methodInvocation.proceed();
		}

		targetClass = ClassKits.getUsefulClass(targetClass);
		Method method = methodInvocation.getMethod();

		// service层的所有拦截器，包含了全局的拦截器 和 @Before 的拦截器
		Interceptor[] serviceInterceptors = InterceptorManager.me().buildServiceMethodInterceptor(INTERS, targetClass, method);
		JFinalBeforeInvocation invocation = new JFinalBeforeInvocation(methodInvocation, serviceInterceptors, methodInvocation.getArguments());
		invocation.invoke();
		return invocation.getReturnValue();
	}

}
