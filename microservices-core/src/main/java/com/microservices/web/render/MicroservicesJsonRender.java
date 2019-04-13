package com.microservices.web.render;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.jfinal.kit.JsonKit;
import com.jfinal.render.JsonRender;
import com.jfinal.render.RenderException;
import com.microservices.MicroservicesConstants;

/**
 * @version V1.0
 * @Package com.microservices.web.render
 */
public class MicroservicesJsonRender extends JsonRender {

	private static final String contentType = "application/json; charset=" + getEncoding();
	private static final String contentTypeForIE = "text/html; charset=" + getEncoding();

	private static final Set<String> excludedAttrs = new HashSet<String>() {
		private static final long serialVersionUID = 9186138395157680676L;

		{
			add("javax.servlet.request.ssl_session");
			add("javax.servlet.request.ssl_session_id");
			add("javax.servlet.request.ssl_session_mgr");
			add("javax.servlet.request.key_size");
			add("javax.servlet.request.cipher_suite");
			add("_res"); // I18nInterceptor 中使用的 _res
			add(MicroservicesConstants.ATTR_REQUEST);
			add(MicroservicesConstants.ATTR_CONTEXT_PATH);
		}
	};

	public MicroservicesJsonRender() {

	}

	public MicroservicesJsonRender(String key, Object value) {
		if (key == null) {
			throw new IllegalArgumentException("The parameter key can not be null.");
		}
		this.jsonText = JsonKit.toJson(new HashMap<String, Object>() {
			{
				put(key, value);
			}
		});
	}

	public MicroservicesJsonRender(String[] attrs) {
		this.attrs = attrs;
	}

	public MicroservicesJsonRender(String jsonText) {
		this.jsonText = jsonText;
	}

	public MicroservicesJsonRender(Object object) {
		this.jsonText = JsonKit.toJson(object);
	}

	private boolean forIE = false;

	@Override
	public JsonRender forIE() {
		forIE = true;
		return this;
	}

	private String jsonText;
	private String[] attrs;

	@Override
	public void render() {

		if (jsonText == null) {
			buildJsonText();
		}

		RenderHelpler.actionCacheExec(jsonText, forIE ? contentTypeForIE : contentType);

		try {
			response.setHeader("Pragma", "no-cache"); // HTTP/1.0 caches might not implement Cache-Control and might only implement Pragma: no-cache
			response.setHeader("Cache-Control", "no-cache");
			response.setDateHeader("Expires", 0);

			response.setContentType(forIE ? contentTypeForIE : contentType);
			PrintWriter writer = response.getWriter();
			writer.write(jsonText);
		} catch (IOException e) {
			throw new RenderException(e);
		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void buildJsonText() {
		Map map = new HashMap();
		if (attrs != null) {
			for (String key : attrs) {
				map.put(key, request.getAttribute(key));
			}
		} else {
			for (Enumeration<String> attrs = request.getAttributeNames(); attrs.hasMoreElements();) {
				String key = attrs.nextElement();
				if (excludedAttrs.contains(key)) {
					continue;
				}

				Object value = request.getAttribute(key);
				map.put(key, value);
			}
		}

		this.jsonText = JsonKit.toJson(map);
	}
}
