package com.microservices.web.cache.keygen;

import javax.servlet.http.HttpServletRequest;

/**
 * @version V1.0
 * @Package com.microservices.web.cache.keygen
 */
public class DefaultActionKeyGeneratorImpl implements IActionKeyGenerator {

	@Override
	public String generate(String target, HttpServletRequest request) {
		String cacheKey = target;
		String queryString = request.getQueryString();
		if (queryString != null) {
			queryString = "?" + queryString;
			cacheKey += queryString;
		}
		return cacheKey;
	}

}
