package com.microservices.web.render;

import com.jfinal.render.ContentType;

/**
 * @version V1.0
 * @Package com.microservices.web.render
 */
public class MicroservicesHtmlRender extends MicroservicesTextRender {
	public MicroservicesHtmlRender(String text) {
		super(text, ContentType.HTML);
	}
}
