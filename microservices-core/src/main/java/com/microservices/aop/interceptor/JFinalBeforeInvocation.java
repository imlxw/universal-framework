package com.microservices.aop.interceptor;

import java.lang.reflect.Method;

import org.aopalliance.intercept.MethodInvocation;

import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;
import com.microservices.exception.MicroservicesException;

/**
 * JFinal的切面操作
 */
public class JFinalBeforeInvocation extends Invocation {

	private Interceptor[] inters;
	private MethodInvocation methodInvocation;
	private Object[] args;

	private int index = 0;

	public JFinalBeforeInvocation(MethodInvocation methodInvocation, Interceptor[] inters, Object[] args) {
		this.methodInvocation = methodInvocation;
		this.inters = inters;
		this.args = args;
	}

	@Override
	public void invoke() {
		if (index < inters.length) {
			inters[index++].intercept(this);
		} else if (index++ == inters.length) { // index++ ensure invoke action only one time
			try {
				setReturnValue(methodInvocation.proceed());
			} catch (Throwable throwable) {
				if (throwable instanceof RuntimeException) {
					throw (RuntimeException) throwable;
				} else {
					throw new MicroservicesException(throwable.getMessage(), throwable);
				}
			}
		}
	}

	@Override
	public Method getMethod() {
		return methodInvocation.getMethod();
	}

	@Override
	public String getMethodName() {
		return getMethod().getName();
	}

	@Override
	public <T> T getTarget() {
		return (T) methodInvocation.getThis();
	}

	@Override
	public Object getArg(int index) {
		if (index >= args.length)
			throw new ArrayIndexOutOfBoundsException();
		return args[index];
	}

	@Override
	public void setArg(int index, Object value) {
		if (index >= args.length)
			throw new ArrayIndexOutOfBoundsException();
		args[index] = value;
	}

	@Override
	public Object[] getArgs() {
		return args;
	}
}
