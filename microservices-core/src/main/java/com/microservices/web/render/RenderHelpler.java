package com.microservices.web.render;

import java.io.PrintWriter;
import java.util.Iterator;

import javax.servlet.http.HttpServletResponse;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.jfinal.render.RenderException;
import com.microservices.Microservices;
import com.microservices.utils.StringUtils;
import com.microservices.web.cache.ActionCacheContent;
import com.microservices.web.cache.ActionCacheContext;
import com.microservices.web.cache.ActionCacheInfo;

/**
 * @version V1.0
 * @Package com.microservices.web.render
 */
public class RenderHelpler {

	public static void actionCacheExec(String html, String contentType) {
		ActionCacheInfo info = ActionCacheContext.get();
		if (info != null) {
			ActionCacheContent actionCache = new ActionCacheContent(contentType, html);
			Microservices.me().getCache().put(info.getGroup(), info.getKey(), actionCache, info.getLiveSeconds());
		}
	}

	public static void renderHtml(HttpServletResponse response, String html, String contentType) {
		response.setContentType(contentType);
		try {
			PrintWriter responseWriter = response.getWriter();
			responseWriter.write(html);
		} catch (Exception e) {
			throw new RenderException(e);
		}
	}

	public static String processCDN(String content) {
		if (StringUtils.isBlank(content)) {
			return content;
		}

		Document doc = Jsoup.parse(content);

		Elements jsElements = doc.select("script[src]");
		replace(jsElements, "src");

		Elements imgElements = doc.select("img[src]");
		replace(imgElements, "src");

		Elements lazyElements = doc.select("img[data-original]");
		replace(lazyElements, "data-original");

		Elements linkElements = doc.select("link[href]");
		replace(linkElements, "href");

		return doc.toString();

	}

	private static void replace(Elements elements, String attrName) {
		String cdnDomain = Microservices.config(MicroservicesRenderConfig.class).getCdn();
		Iterator<Element> iterator = elements.iterator();
		while (iterator.hasNext()) {

			Element element = iterator.next();

			if (element.hasAttr("cdn-exclude")) {
				continue;
			}

			String url = element.attr(attrName);
			if (StringUtils.isBlank(url) || !url.startsWith("/") || url.startsWith("//")) {
				continue;
			}

			url = cdnDomain + url;

			element.attr(attrName, url);
		}
	}
}
