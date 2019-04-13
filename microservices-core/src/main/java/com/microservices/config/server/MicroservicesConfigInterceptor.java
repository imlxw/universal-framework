package com.microservices.config.server;

import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;
import com.jfinal.kit.Ret;
import com.microservices.Microservices;
import com.microservices.config.MicroservicesConfigConfig;

public class MicroservicesConfigInterceptor implements Interceptor {

	static MicroservicesConfigConfig config = Microservices.config(MicroservicesConfigConfig.class);

	@Override
	public void intercept(Invocation inv) {
		if (!config.isServerEnable()) {
			inv.getController().renderJson(Ret.fail("msg", "sorry,  you have no permission to visit this page. "));
			return;
		}

		inv.invoke();
	}
}
