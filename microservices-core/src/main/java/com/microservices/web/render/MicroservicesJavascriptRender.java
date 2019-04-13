package com.microservices.web.render;

import com.jfinal.render.ContentType;

/**
 * @version V1.0
 * @Package com.microservices.web.render
 */
public class MicroservicesJavascriptRender extends MicroservicesTextRender {
	public MicroservicesJavascriptRender(String text) {
		super(text, ContentType.JAVASCRIPT);
	}
}
