package com.microservices.web.cors;

import javax.servlet.http.HttpServletResponse;

import com.microservices.utils.StringUtils;
import com.microservices.web.fixedinterceptor.FixedInterceptor;
import com.microservices.web.fixedinterceptor.FixedInvocation;

/**
 * @version V1.0
 * @Title: CORS 处理相关 拦截器
 * @Package com.microservices.web.cors
 */
public class CORSInterceptor implements FixedInterceptor {

	private static final String ALLOW_ORIGIN = "*";
	private static final String ALLOW_METHODS = "GET, POST, PUT, DELETE, OPTIONS";
	private static final int MAX_AGE = 3600;

	@Override
	public void intercept(FixedInvocation inv) {
		EnableCORS enableCORS = inv.getMethod().getAnnotation(EnableCORS.class);
		if (enableCORS == null) {
			enableCORS = inv.getController().getClass().getAnnotation(EnableCORS.class);
		}
		if (enableCORS != null) {
			doProcessCORS(inv, enableCORS);
		}

		inv.invoke();
	}

	private void doProcessCORS(FixedInvocation inv, EnableCORS enableCORS) {
		HttpServletResponse response = inv.getController().getResponse();

		String allowOrigin = enableCORS.allowOrigin();
		String allowCredentials = enableCORS.allowCredentials();
		String allowHeaders = enableCORS.allowHeaders();
		String allowMethods = enableCORS.allowMethods();
		String exposeHeaders = enableCORS.exposeHeaders();
		String requestHeaders = enableCORS.requestHeaders();
		String requestMethod = enableCORS.requestMethod();
		String origin = enableCORS.origin();
		int maxAge = enableCORS.maxAge();

		allowOrigin = StringUtils.isNotBlank(allowOrigin) ? allowOrigin : ALLOW_ORIGIN;
		allowMethods = StringUtils.isNotBlank(allowMethods) ? allowMethods : ALLOW_METHODS;
		maxAge = maxAge > 0 ? maxAge : MAX_AGE;

		response.setHeader("Access-Control-Allow-Origin", allowOrigin);
		response.setHeader("Access-Control-Allow-Methods", allowMethods);
		response.setHeader("Access-Control-Max-Age", String.valueOf(maxAge));

		if (StringUtils.isNotBlank(allowHeaders)) {
			response.setHeader("Access-Control-Allow-Headers", allowHeaders);
		}

		if (StringUtils.isNotBlank(allowCredentials)) {
			response.setHeader("Access-Control-Allow-Credentials", allowCredentials);
		}

		if (StringUtils.isNotBlank(exposeHeaders)) {
			response.setHeader("Access-Control-Expose-Headers", exposeHeaders);
		}

		if (StringUtils.isNotBlank(requestHeaders)) {
			response.setHeader("Access-Control-Request-Headers", requestHeaders);
		}

		if (StringUtils.isNotBlank(requestMethod)) {
			response.setHeader("Access-Control-Request-Method", requestMethod);
		}

		if (StringUtils.isNotBlank(origin)) {
			response.setHeader("Origin", origin);
		}

	}
}
