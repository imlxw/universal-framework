package com.microservices.config.server;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.jfinal.aop.Before;
import com.jfinal.aop.Clear;
import com.microservices.Microservices;
import com.microservices.config.MicroservicesConfigConfig;
import com.microservices.config.MicroservicesConfigManager;
import com.microservices.config.PropInfoMap;
import com.microservices.utils.StringUtils;
import com.microservices.web.controller.MicroservicesController;
import com.microservices.web.controller.annotation.RequestMapping;

/**
 * 配置文件的Controller，用于给其他应用提供分布式配置读取功能
 */
@Clear
@RequestMapping("/microservices/config")
@Before(MicroservicesConfigInterceptor.class)
public class MicroservicesConfigController extends MicroservicesController {

	MicroservicesConfigConfig config = Microservices.config(MicroservicesConfigConfig.class);

	public void index() {
		String id = getPara();
		if (StringUtils.isBlank(id)) {
			renderJson(MicroservicesConfigManager.me().getPropInfoMap());
			return;
		} else {
			PropInfoMap propInfos = MicroservicesConfigManager.me().getPropInfoMap();
			for (PropInfoMap.Entry<String, PropInfoMap.PropInfo> entry : propInfos.entrySet()) {
				if (id.equals(entry.getKey())) {
					renderJson(PropInfoMap.create(entry.getKey(), entry.getValue()));
					return;
				}
			}
		}
		renderJson("{}");
	}

	/**
	 * 列出本地目录下的文件信息
	 */
	public void list() {
		List<HashMap<String, String>> props = new ArrayList<>();
		PropInfoMap propInfos = MicroservicesConfigManager.me().getPropInfoMap();
		for (PropInfoMap.Entry<String, PropInfoMap.PropInfo> entry : propInfos.entrySet()) {
			HashMap<String, String> prop = new HashMap<>();
			prop.put("id", entry.getKey());
			prop.put("version", entry.getValue().getVersion());
			props.add(prop);
		}
		renderJson(props.toArray());
	}
}
