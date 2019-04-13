package com.microservices.web.cache;

import java.io.Serializable;

/**
 * @version V1.0
 * @Package com.microservices.web.cache
 */
public class ActionCacheContent implements Serializable {

	private String contentType;
	private String content;

	public ActionCacheContent() {

	}

	public ActionCacheContent(String contentType, String content) {
		this.contentType = contentType;
		this.content = content;
	}

	public String getContentType() {
		return contentType;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}
}
