package com.microservices.web.render;

import java.io.IOException;
import java.io.PrintWriter;

import com.jfinal.render.ContentType;
import com.jfinal.render.RenderException;
import com.jfinal.render.TextRender;

/**
 * @version V1.0
 * @Package com.microservices.web.render
 */
public class MicroservicesTextRender extends TextRender {

	// 与 encoding 与 contentType 在 render() 方法中分开设置，效果相同
	private static final String DEFAULT_CONTENT_TYPE = "text/plain";

	private String text;
	private String contentType;

	public MicroservicesTextRender(String text) {
		super(text);
		this.text = text;
		this.contentType = DEFAULT_CONTENT_TYPE;
	}

	public MicroservicesTextRender(String text, String contentType) {
		super(text, contentType);
		this.text = text;
		this.contentType = contentType;
	}

	public MicroservicesTextRender(String text, ContentType contentType) {
		super(text, contentType);
		this.text = text;
		this.contentType = contentType.value();
	}

	@Override
	public void render() {
		try {

			RenderHelpler.actionCacheExec(text, contentType);

			response.setHeader("Pragma", "no-cache"); // HTTP/1.0 caches might not implement Cache-Control and might only implement Pragma: no-cache
			response.setHeader("Cache-Control", "no-cache");
			response.setDateHeader("Expires", 0);

			response.setContentType(contentType);
			response.setCharacterEncoding(getEncoding()); // 与 contentType 分开设置

			PrintWriter writer = response.getWriter();
			writer.write(text);
		} catch (IOException e) {
			throw new RenderException(e);
		}
	}

	@Override
	public String getText() {
		return text;
	}

	@Override
	public String getContentType() {
		return contentType;
	}
}
