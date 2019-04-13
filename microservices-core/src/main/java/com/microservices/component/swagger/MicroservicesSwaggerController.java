package com.microservices.component.swagger;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializeConfig;
import com.google.common.collect.Maps;
import com.microservices.Microservices;
import com.microservices.web.controller.MicroservicesController;
import com.microservices.web.cors.EnableCORS;

import io.swagger.models.Swagger;
import io.swagger.models.properties.RefProperty;

/**
 * @version V1.0
 * @Package com.microservices.component.swagger
 */
public class MicroservicesSwaggerController extends MicroservicesController {

	MicroservicesSwaggerConfig config = Microservices.config(MicroservicesSwaggerConfig.class);

	public void index() {
		String html = null;
		String viewPath = config.getPath().endsWith("/") ? config.getPath() : config.getPath() + "/";
		try {
			html = renderToString(viewPath + "index.html", Maps.newHashMap());
		} catch (Throwable ex) {
		}

		if (html == null) {
			renderHtml("error，please put  <a href=\"https://github.com/swagger-api/swagger-ui\" target=\"_blank\">swagger-ui</a> into your project path :  " + config.getPath() + " <br />" + "or click <a href=\"" + config.getPath()
					+ "/json\">here</a>  show swagger json.");
			return;
		}

		html = html.replace("http://petstore.swagger.io/v2/swagger.json", getRequest().getRequestURL() + "/json");
		html = html.replace("src=\"./", "src=\"" + config.getPath() + "/");
		html = html.replace("href=\"./", "href=\"" + config.getPath() + "/");

		renderHtml(html);
	}

	/**
	 * 渲染json 参考：http://petstore.swagger.io/ 及json信息 http://petstore.swagger.io/v2/swagger.json
	 */
	@EnableCORS
	public void json() {
		Swagger swagger = MicroservicesSwaggerManager.me().getSwagger();
		if (swagger == null) {
			renderText("swagger config error.");
			return;
		}

		// 适配swaggerUI, 解决页面"Unknown Type : ref"问题。
		SerializeConfig serializeConfig = new SerializeConfig();
		serializeConfig.put(RefProperty.class, new RefPropertySerializer());
		renderJson(JSON.toJSONString(swagger, serializeConfig));
	}

}
