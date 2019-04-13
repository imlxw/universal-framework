package com.microservices.web.limitation.web;

import java.util.HashMap;

import com.jfinal.aop.Before;
import com.jfinal.kit.Ret;
import com.microservices.utils.StringUtils;
import com.microservices.web.controller.MicroservicesController;
import com.microservices.web.limitation.MicroservicesLimitationManager;

/**
 * @version V1.0
 * @Package com.microservices.web.limitation
 *          <p>
 *          使用步骤： 1、通过 microservices.limitation.viewPaht 设置词controller 的访问路径，例如：设置为 /limitation 2、浏览器访问 /limitation 查看所有限流情况 3、浏览器访问 /limitation/set?path=/aaa/bbb/ccc&rate=111&type=ip 来这是单个ip限流情况 4、浏览器访问
 *          /limitation/close?path=/aaa/bbb/ccc&type=ip 来关闭/aaa/bbb/ccc对ip的限流情况 5、浏览器访问 /limitation/enable?path=/aaa/bbb/ccc&type=ip 来开启/aaa/bbb/ccc对ip的限流情况
 *          <p>
 *          其他： 由于设置限流非常重要，可以通过 microservices.limitation.webAuthorizer = com.xxx.MyAuthorizer 来设置访问的 /limitation 的授权控制 MyAuthorizer 要实现接口 com.microservices.web.limitation.web.Authorizer
 */
@Before(LimitationControllerInter.class)
public class LimitationController extends MicroservicesController {

	MicroservicesLimitationManager manager = MicroservicesLimitationManager.me();

	public void index() {

		HashMap info = new HashMap();

		info.put("ipRates", manager.getIpRates());
		info.put("userRates", manager.getUserRates());
		info.put("requestRates", manager.getRequestRates());
		info.put("concurrencyRates", manager.getConcurrencyRates());

		renderJson(info);
	}

	public void set() {
		String path = getPara("path");
		String rateString = getPara("rate");
		String type = getPara("type");

		double rate = StringUtils.isBlank(rateString) ? 0 : Double.valueOf(rateString.trim());

		if (StringUtils.isBlank(type)) {
			renderJson(Ret.fail().set("message", "type is empty"));
			return;
		}

		if (StringUtils.isBlank(path)) {
			renderJson(Ret.fail().set("message", "path is empty"));
			return;
		}

		if (rate <= 0) {
			renderJson(Ret.fail().set("message", "rate is error"));
			return;
		}

		switch (type) {
			case "ip":
				manager.setIpRates(path, rate);
				break;
			case "user":
				manager.setUserRates(path, rate);
				break;
			case "request":
				manager.setRequestRates(path, rate);
				break;
			case "concurrency":
				manager.setConcurrencyRates(path, rate);
				break;
			default:
				renderJson(Ret.fail().set("message", "type is error"));
				return;
		}

		renderJson(Ret.ok().set("message", "set ok"));
	}

	public void enable() {
		Ret ret = manager.doProcessEnable(getPara("path"), getPara("type"), true);
		if (ret.isOk()) {
			ret.set("message", "enable ok");
		}
		renderJson(ret);
	}

	public void close() {
		Ret ret = manager.doProcessEnable(getPara("path"), getPara("type"), false);
		if (ret.isOk()) {
			ret.set("message", "close ok");
		}
		renderJson(ret);
	}

}
