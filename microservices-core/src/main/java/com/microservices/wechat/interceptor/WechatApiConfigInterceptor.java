package com.microservices.wechat.interceptor;

import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;
import com.jfinal.weixin.sdk.api.ApiConfig;
import com.jfinal.weixin.sdk.api.ApiConfigKit;
import com.microservices.wechat.controller.MicroservicesWechatController;

public class WechatApiConfigInterceptor implements Interceptor {

	@Override
	public void intercept(Invocation inv) {
		try {
			MicroservicesWechatController controller = (MicroservicesWechatController) inv.getController();
			ApiConfig config = controller.getApiConfig();

			if (config == null) {
				inv.getController().renderText("error : cannot get apiconfig,please config microservices.properties");
				return;
			}

			ApiConfigKit.setThreadLocalAppId(config.getAppId());
			inv.invoke();
		} finally {
			ApiConfigKit.removeThreadLocalAppId();
		}
	}

}
