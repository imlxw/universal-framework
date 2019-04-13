package com.microservices.web.render;

import com.microservices.config.annotation.PropertyConfig;
import com.microservices.utils.StringUtils;

@PropertyConfig(prefix = "microservices.render")
public class MicroservicesRenderConfig {

	private String cdn;

	public String getCdn() {
		return cdn;
	}

	public void setCdn(String cdn) {
		this.cdn = cdn;
	}

	public boolean isEnableCdn() {
		return StringUtils.isNotBlank(cdn);
	}
}
