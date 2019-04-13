package com.microservices.admin.controller;

import com.jfinal.aop.Before;
import com.jfinal.ext.interceptor.POST;
import com.microservices.admin.base.common.Consts;
import com.microservices.admin.base.common.RestResult;
import com.microservices.admin.base.plugin.shiro.MuitiLoginToken;
import com.microservices.admin.base.web.base.BaseController;
import com.microservices.admin.service.api.UserService;
import com.microservices.admin.service.entity.model.User;
import com.microservices.admin.validator.LoginValidator;
import com.microservices.core.rpc.annotation.MicroservicesrpcService;
import com.microservices.web.controller.annotation.RequestMapping;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.ExcessiveAttemptsException;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.LockedAccountException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.subject.Subject;

/**
 * 主控制器
 */
@RequestMapping("/")
public class MainController extends BaseController {

	@MicroservicesrpcService
	private UserService userService;

	public void index() {
		render("index.html");
	}

	public void login() {
		if (SecurityUtils.getSubject().isAuthenticated()) {
			redirect("/");
		} else {
			render("login.html");
		}
	}

	public void captcha() {
		renderCaptcha();
	}

	@Before({ POST.class, LoginValidator.class })
	public void postLogin() {
		String loginName = getPara("loginName");
		String pwd = getPara("password");

		MuitiLoginToken token = new MuitiLoginToken(loginName, pwd);
		Subject subject = SecurityUtils.getSubject();

		RestResult<String> restResult = new RestResult<String>();
		restResult.success().setMsg("登录成功");

		try {
			if (!subject.isAuthenticated()) {
				token.setRememberMe(false);
				subject.login(token);

				User u = userService.findByName(loginName);
				subject.getSession(true).setAttribute(Consts.SESSION_USER, u);
			}
			if (getParaToBoolean("rememberMe") != null && getParaToBoolean("rememberMe")) {
				setCookie("loginName", loginName, 60 * 60 * 24 * 7);
			} else {
				removeCookie("loginName");
			}
		} catch (UnknownAccountException une) {
			restResult.error("用户名不存在");
		} catch (LockedAccountException lae) {
			restResult.error("用户被锁定");
		} catch (IncorrectCredentialsException ine) {
			restResult.error("用户名或密码不正确");
		} catch (ExcessiveAttemptsException exe) {
			restResult.error("账户密码错误次数过多，账户已被限制登录1小时");
		} catch (Exception e) {
			e.printStackTrace();
			restResult.error("服务异常，请稍后重试");
		}

		renderJson(restResult);
	}

	/**
	 * 退出
	 */
	public void logout() {
		if (SecurityUtils.getSubject().isAuthenticated()) {
			SecurityUtils.getSubject().logout();
		}
		render("login.html");
	}

	public void welcome() {
		render("welcome.html");
	}

}
