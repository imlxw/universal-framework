package com.microservices.component.shiro;

import com.microservices.Microservices;
import com.microservices.component.shiro.processer.AuthorizeResult;
import com.microservices.web.fixedinterceptor.FixedInterceptor;
import com.microservices.web.fixedinterceptor.FixedInvocation;

/**
 * Shiro 拦截器
 */
public class MicroservicesShiroInterceptor implements FixedInterceptor {

	private static MicroservicesShiroConfig config = Microservices.config(MicroservicesShiroConfig.class);

	@Override
	public void intercept(FixedInvocation inv) {
		if (!config.isConfigOK()) {
			inv.invoke();
			return;
		}

		MicroservicesShiroManager.me().getInvokeListener().onInvokeBefore(inv);
		AuthorizeResult result = MicroservicesShiroManager.me().invoke(inv.getActionKey());
		MicroservicesShiroManager.me().getInvokeListener().onInvokeAfter(inv, result == null ? AuthorizeResult.ok() : result);
	}

}
