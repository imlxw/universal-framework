package com.microservices.web.limitation.web;

import com.jfinal.core.Controller;

/**
 * @version V1.0
 * @Package com.microservices.web.limitation.web
 */
public interface Authorizer {

	public boolean onAuthorize(Controller controller);
}
