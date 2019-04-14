package com.framework.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.framework.annotation.Login;
import com.framework.annotation.LoginUser;
import com.framework.common.utils.R;
import com.framework.entity.UserEntity;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import springfox.documentation.annotations.ApiIgnore;

/**
 * 测试接口
 */
@RestController
@RequestMapping("/api")
@Api(tags = "测试接口")
public class ApiTestController {

	@Login
	@GetMapping("userInfo")
	@ApiOperation(value = "获取用户信息", response = UserEntity.class)
	public R userInfo(@ApiIgnore @LoginUser UserEntity user) {
		return R.ok().put("user", user);
	}

	@Login
	@GetMapping("userId")
	@ApiOperation("获取用户ID")
	public R userInfo(@ApiIgnore @RequestAttribute("userId") Integer userId) {
		return R.ok().put("userId", userId);
	}

	@GetMapping("notToken")
	@ApiOperation("忽略Token验证测试")
	public R notToken() {
		return R.ok().put("msg", "无需token也能访问。。。");
	}

}
