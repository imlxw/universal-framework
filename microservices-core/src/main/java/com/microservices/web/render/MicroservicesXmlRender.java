package com.microservices.web.render;

/**
 * @version V1.0
 * @Package com.microservices.web.render
 */
public class MicroservicesXmlRender extends MicroservicesRender {

	private static final String contentType = "text/xml; charset=" + getEncoding();

	public MicroservicesXmlRender(String view) {
		super(view);
	}

	@Override
	public String getContentType() {
		return contentType;
	}
}
