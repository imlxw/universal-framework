package com.microservices.admin.support.auth;

import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;
import com.microservices.Microservices;
import com.microservices.admin.service.api.ResService;
import com.microservices.admin.service.entity.model.Res;
import com.microservices.admin.service.entity.status.system.ResStatus;
import org.apache.shiro.SecurityUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 根据url的权限拦截器，具有url权限的角色才允许访问
 */
public class AuthInterceptor implements Interceptor {

	/**
	 * 获取全部 需要控制的权限
	 */
	private static List<String> urls;

	public AuthInterceptor() {

	}

	public static List<String> getUrls() {
		return urls;
	}

	public static void init() {
		ResService sysResApi = Microservices.service(ResService.class);
		List<Res> sysResList = sysResApi.findByStatus(ResStatus.USED);
		List<String> list = new ArrayList<String>();
		for (Res res : sysResList) {
			list.add(res.getUrl());
		}
		urls = list;
	}

	@Override
	public void intercept(Invocation ai) {
		if (urls == null) {
			init();
		}

		String url = ai.getActionKey();
		boolean flag = SecurityUtils.getSubject() != null && SecurityUtils.getSubject().isPermitted(url);

		if (urls.contains(url) && !flag) {
			ai.getController().renderError(403);
		} else {
			ai.invoke();
		}
	}

}
