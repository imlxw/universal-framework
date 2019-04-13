package com.microservices.web.limitation.web;

import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;
import com.microservices.Microservices;
import com.microservices.exception.MicroservicesException;
import com.microservices.utils.ClassKits;
import com.microservices.web.limitation.LimitationConfig;

/**
 * @version V1.0
 * @Package com.microservices.web.limitation.web
 */
public class LimitationControllerInter implements Interceptor {

	@Override
	public void intercept(Invocation inv) {

		if (!getAuthorizer().onAuthorize(inv.getController())) {
			inv.getController().renderError(404);
			return;
		}

		inv.invoke();
	}

	private static Authorizer authorizer;

	private Authorizer getAuthorizer() {

		if (authorizer == null) {
			String authorizerClass = Microservices.config(LimitationConfig.class).getWebAuthorizer();
			authorizer = ClassKits.newInstance(authorizerClass);
			if (authorizer == null) {
				throw new MicroservicesException("can not init authorizer for class : " + authorizerClass);
			}
		}

		return authorizer;
	}

}
