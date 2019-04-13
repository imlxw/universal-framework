package com.microservices.web.limitation;

import com.microservices.config.annotation.PropertyConfig;
import com.microservices.web.limitation.web.NoneAuthorizer;

/**
 * @version V1.0
 * @Package com.microservices.web.limitation
 */
@PropertyConfig(prefix = "microservices.limitation")
public class LimitationConfig {

	private int limitAjaxCode = 886;
	private String limitAjaxMessage = "request limit";

	private String limitView;

	private String webPath;
	private String webAuthorizer = NoneAuthorizer.class.getName();

	public int getLimitAjaxCode() {
		return limitAjaxCode;
	}

	public void setLimitAjaxCode(int limitAjaxCode) {
		this.limitAjaxCode = limitAjaxCode;
	}

	public String getLimitAjaxMessage() {
		return limitAjaxMessage;
	}

	public void setLimitAjaxMessage(String limitAjaxMessage) {
		this.limitAjaxMessage = limitAjaxMessage;
	}

	public String getLimitView() {
		return limitView;
	}

	public void setLimitView(String limitView) {
		this.limitView = limitView;
	}

	public String getWebPath() {
		return webPath;
	}

	public void setWebPath(String webPath) {
		this.webPath = webPath;
	}

	public String getWebAuthorizer() {
		return webAuthorizer;
	}

	public void setWebAuthorizer(String webAuthorizer) {
		this.webAuthorizer = webAuthorizer;
	}
}
