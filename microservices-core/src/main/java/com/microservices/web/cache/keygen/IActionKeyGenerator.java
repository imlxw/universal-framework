package com.microservices.web.cache.keygen;

import javax.servlet.http.HttpServletRequest;

/**
 * @version V1.0
 * @Package com.microservices.web.cache.keygen
 */
public interface IActionKeyGenerator {

	public String generate(String target, HttpServletRequest request);
}
