package com.microservices.web.render;

import com.jfinal.render.ContentType;
import com.jfinal.render.Render;
import com.jfinal.render.RenderFactory;

public class MicroservicesRenderFactory extends RenderFactory {

	private static final MicroservicesRenderFactory ME = new MicroservicesRenderFactory();

	public static final MicroservicesRenderFactory me() {
		return ME;
	}

	@Override
	public Render getRender(String view) {
		return new MicroservicesRender(view);
	}

	@Override
	public Render getHtmlRender(String htmlText) {
		return new MicroservicesHtmlRender(htmlText);
	}

	@Override
	public Render getTextRender(String text) {
		return new MicroservicesTextRender(text);
	}

	@Override
	public Render getTextRender(String text, String contentType) {
		return new MicroservicesTextRender(text, contentType);
	}

	@Override
	public Render getTextRender(String text, ContentType contentType) {
		return new MicroservicesTextRender(text, contentType);
	}

	@Override
	public Render getJavascriptRender(String jsText) {
		return new MicroservicesJavascriptRender(jsText);
	}

	@Override
	public Render getErrorRender(int errorCode) {
		return new MicroservicesErrorRender(errorCode, constants.getErrorView(errorCode));
	}

	@Override
	public Render getErrorRender(int errorCode, String view) {
		return new MicroservicesErrorRender(errorCode, view);
	}

	@Override
	public Render getJsonRender() {
		return new MicroservicesJsonRender();
	}

	@Override
	public Render getJsonRender(String key, Object value) {
		return new MicroservicesJsonRender(key, value);
	}

	@Override
	public Render getJsonRender(String[] attrs) {
		return new MicroservicesJsonRender(attrs);
	}

	@Override
	public Render getJsonRender(String jsonText) {
		return new MicroservicesJsonRender(jsonText);
	}

	@Override
	public Render getJsonRender(Object object) {
		return new MicroservicesJsonRender(object);
	}

	@Override
	public Render getTemplateRender(String view) {
		return new MicroservicesTemplateRender(view);
	}

	@Override
	public Render getXmlRender(String view) {
		return new MicroservicesXmlRender(view);
	}

}
