package com.microservices.b2c.controller;

import com.jfinal.kit.Ret;
import com.microservices.Microservices;
import com.microservices.admin.base.common.CacheKey;
import com.microservices.admin.base.common.RestResult;
import com.microservices.admin.base.exception.BusinessException;
import com.microservices.admin.base.interceptor.NotNullPara;
import com.microservices.admin.base.web.base.BaseController;
import com.microservices.web.controller.annotation.RequestMapping;
import org.apache.shiro.authz.annotation.RequiresAuthentication;

/**
 * 主控制器
 */
@RequestMapping("/")
public class MainController extends BaseController {

	@RequiresAuthentication
	public void index() {
		renderJson(RestResult.buildSuccess(Ret.create("userId", getJwtPara("userId"))));
	}

	/**
	 * 调转登录页面
	 */
	public void login() {

	}

	/**
	 * 登录 基于 jwt
	 */
	@NotNullPara({ "loginName", "pwd" })
	public void postLogin(String loginName, String pwd) {

		// 此处为 demo , 实际项目这里应该调用service 判断
		if ("user1".equals(loginName) && "123456".equals(pwd)) {
			String userId = "user1";

			// 登录成功移除用户退出标识
			Microservices.me().getCache().remove(CacheKey.CACHE_JWT_TOKEN, userId);
			setJwtAttr("userId", userId);
		} else {
			throw new BusinessException("用户名密码错误");
		}

		renderJson(RestResult.buildSuccess("登录成功"));
	}

	@RequiresAuthentication
	public void logout() {
		// 退出后将 jwt 加入黑名单
		Microservices.me().getCache().put(CacheKey.CACHE_JWT_TOKEN, getJwtPara("userId"), getJwtPara("userId"), 2 * 60 * 60);
		renderJson(RestResult.buildSuccess("退出成功"));
	}
}
