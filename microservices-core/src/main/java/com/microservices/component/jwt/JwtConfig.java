package com.microservices.component.jwt;

import com.microservices.config.annotation.PropertyConfig;
import com.microservices.utils.StringUtils;

/**
 * @Package com.microservices.web.jwt
 */
@PropertyConfig(prefix = "microservices.web.jwt")
public class JwtConfig {

	private String httpHeaderName = "Jwt";
	private String secret;
	private long validityPeriod = 0;

	public String getHttpHeaderName() {
		return httpHeaderName;
	}

	public void setHttpHeaderName(String httpHeaderName) {
		this.httpHeaderName = httpHeaderName;
	}

	public String getSecret() {
		return secret;
	}

	public void setSecret(String secret) {
		this.secret = secret;
	}

	public long getValidityPeriod() {
		return validityPeriod;
	}

	public void setValidityPeriod(long validityPeriod) {
		this.validityPeriod = validityPeriod;
	}

	public boolean isEnable() {
		return StringUtils.isNotBlank(secret);
	}

}
