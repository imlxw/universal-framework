package com.microservices.component.hystrix;

import com.microservices.config.annotation.PropertyConfig;

@PropertyConfig(prefix = "microservices.hystrix")
public class MicroservicesHystrixConfig {

	private String url;
	private String propertie;

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getPropertie() {
		return propertie;
	}

	public void setPropertie(String propertie) {
		this.propertie = propertie;
	}

}
