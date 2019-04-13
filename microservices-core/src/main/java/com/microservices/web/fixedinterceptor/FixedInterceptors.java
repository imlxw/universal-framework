package com.microservices.web.fixedinterceptor;

import java.util.ArrayList;
import java.util.List;

import com.microservices.Microservices;
import com.microservices.component.jwt.JwtInterceptor;
import com.microservices.component.metric.MicroservicesMetricInterceptor;
import com.microservices.component.opentracing.OpentracingInterceptor;
import com.microservices.component.shiro.MicroservicesShiroInterceptor;
import com.microservices.web.controller.validate.ParaValidateInterceptor;
import com.microservices.web.cors.CORSInterceptor;
import com.microservices.web.limitation.LimitationInterceptor;

/**
 * @version V1.0
 * @Package com.microservices.web.fixedinterceptor
 */
public class FixedInterceptors {

	private static final FixedInterceptors me = new FixedInterceptors();

	public static FixedInterceptors me() {
		return me;
	}

	/**
	 * 默认的 Microservices 系统拦截器
	 */
	private FixedInterceptor[] defaultInters = new FixedInterceptor[] { new CORSInterceptor(), new LimitationInterceptor(), new ParaValidateInterceptor(), new JwtInterceptor(), new MicroservicesShiroInterceptor(),
			new OpentracingInterceptor(), new MicroservicesMetricInterceptor() };

	private List<FixedInterceptor> userInters = new ArrayList<>();

	private FixedInterceptor[] allInters = null;

	FixedInterceptor[] all() {
		if (allInters == null) {
			initInters();
		}
		return allInters;
	}

	private void initInters() {
		allInters = new FixedInterceptor[defaultInters.length + userInters.size()];

		int i = 0;
		for (FixedInterceptor interceptor : defaultInters) {
			Microservices.injectMembers(interceptor);
			allInters[i++] = interceptor;
		}

		for (FixedInterceptor interceptor : userInters) {
			Microservices.injectMembers(interceptor);
			allInters[i++] = interceptor;
		}
	}

	public void add(FixedInterceptor interceptor) {
		userInters.add(interceptor);
	}

	public List<FixedInterceptor> list() {
		return userInters;
	}
}
