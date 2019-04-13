package com.microservices.web.render;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import com.jfinal.render.Render;
import com.jfinal.render.RenderManager;
import com.jfinal.template.Engine;
import com.microservices.Microservices;

public class MicroservicesRender extends Render {

	private static Engine engine;

	private static final String contentType = "text/html; charset=" + getEncoding();

	private Engine getEngine() {
		if (engine == null) {
			engine = RenderManager.me().getEngine();
		}
		return engine;
	}

	private MicroservicesRenderConfig config;

	public MicroservicesRender(String view) {
		this.view = view;
		this.config = Microservices.config(MicroservicesRenderConfig.class);
	}

	public String getContentType() {
		return contentType;
	}

	@Override
	public void render() {
		response.setContentType(getContentType());

		Map<Object, Object> data = new HashMap<Object, Object>();
		for (Enumeration<String> attrs = request.getAttributeNames(); attrs.hasMoreElements();) {
			String attrName = attrs.nextElement();
			data.put(attrName, request.getAttribute(attrName));
		}

		String html = getEngine().getTemplate(view).renderToString(data);
		html = config.isEnableCdn() ? RenderHelpler.processCDN(html) : html;

		RenderHelpler.actionCacheExec(html, contentType);

		RenderHelpler.renderHtml(response, html, contentType);
	}

	@Override
	public String toString() {
		return view;
	}

}
