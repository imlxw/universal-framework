package com.microservices.web.fixedinterceptor;

import java.lang.reflect.Method;

import com.jfinal.aop.Invocation;
import com.jfinal.core.Controller;

/**
 * @version V1.0
 * @Package com.microservices.web.handler
 */
public class FixedInvocation {

	private Invocation invocation;

	private FixedInterceptor[] inters = FixedInterceptors.me().all();
	private int index = 0;

	public FixedInvocation(Invocation invocation) {
		this.invocation = invocation;
	}

	public void invoke() {
		if (index < inters.length) {
			inters[index++].intercept(this);
		} else if (index++ == inters.length) { // index++ ensure invoke action only one time
			invocation.invoke();
		}
	}

	public Method getMethod() {
		return invocation.getMethod();
	}

	public Controller getController() {
		return invocation.getController();
	}

	public String getActionKey() {
		return invocation.getActionKey();
	}

	public String getControllerKey() {
		return invocation.getControllerKey();
	}

	public String getMethodName() {
		return invocation.getMethodName();
	}

	public Invocation getInvocation() {
		return invocation;
	}
}
