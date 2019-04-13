package com.microservices.component.jwt;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.microservices.utils.StringUtils;
import com.microservices.web.controller.MicroservicesController;
import com.microservices.web.fixedinterceptor.FixedInterceptor;
import com.microservices.web.fixedinterceptor.FixedInvocation;

/**
 * @Title: 用于对Jwt的设置
 * @Package com.microservices.web.jwt
 */
public class JwtInterceptor implements FixedInterceptor {

	@Override
	public void intercept(FixedInvocation inv) {
		if (!JwtManager.me().isEnable()) {
			inv.invoke();
			return;
		}

		HttpServletRequest request = inv.getController().getRequest();
		String token = request.getHeader(JwtManager.me().getHttpHeaderName());

		if (StringUtils.isBlank(token)) {
			inv.invoke();
			processInvokeAfter(inv);
			return;
		}

		Map map = JwtManager.me().parseJwtToken(token);
		if (map == null) {
			inv.invoke();
			processInvokeAfter(inv);
			return;
		}

		try {
			JwtManager.me().holdJwts(map);
			inv.invoke();
			processInvokeAfter(inv);
		} finally {
			JwtManager.me().releaseJwts();
		}
	}

	private void processInvokeAfter(FixedInvocation inv) {
		if (!(inv.getController() instanceof MicroservicesController)) {
			return;
		}

		MicroservicesController microservicesController = (MicroservicesController) inv.getController();
		Map<String, Object> jwtMap = microservicesController.getJwtAttrs();

		if (jwtMap == null || jwtMap.isEmpty()) {
			return;
		}

		String token = JwtManager.me().createJwtToken(jwtMap);
		HttpServletResponse response = inv.getController().getResponse();
		response.addHeader(JwtManager.me().getHttpHeaderName(), token);
	}
}
