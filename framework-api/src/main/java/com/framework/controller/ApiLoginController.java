package com.framework.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.framework.annotation.Login;
import com.framework.common.utils.R;
import com.framework.common.validator.ValidatorUtils;
import com.framework.form.LoginForm;
import com.framework.service.TokenService;
import com.framework.service.UserService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import springfox.documentation.annotations.ApiIgnore;

/**
 * 登录接口
 */
@RestController
@RequestMapping("/api")
@Api(tags = "登录接口")
public class ApiLoginController {
	@Autowired
	private UserService userService;
	@Autowired
	private TokenService tokenService;

	@PostMapping("login")
	@ApiOperation("登录")
	public R login(@RequestBody LoginForm form) {
		// 表单校验
		ValidatorUtils.validateEntity(form);

		// 用户登录
		Map<String, Object> map = userService.login(form);

		return R.ok(map);
	}

	@Login
	@PostMapping("logout")
	@ApiOperation("退出")
	public R logout(@ApiIgnore @RequestAttribute("userId") long userId) {
		tokenService.expireToken(userId);
		return R.ok();
	}

}
