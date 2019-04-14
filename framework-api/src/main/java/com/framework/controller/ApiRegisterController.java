package com.framework.controller;

import java.util.Date;

import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.framework.common.utils.R;
import com.framework.common.validator.ValidatorUtils;
import com.framework.entity.UserEntity;
import com.framework.form.RegisterForm;
import com.framework.service.UserService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * 注册接口
 */
@RestController
@RequestMapping("/api")
@Api(tags = "注册接口")
public class ApiRegisterController {
	@Autowired
	private UserService userService;

	@PostMapping("register")
	@ApiOperation("注册")
	public R register(@RequestBody RegisterForm form) {
		// 表单校验
		ValidatorUtils.validateEntity(form);

		UserEntity user = new UserEntity();
		user.setMobile(form.getMobile());
		user.setUsername(form.getMobile());
		user.setPassword(DigestUtils.sha256Hex(form.getPassword()));
		user.setCreateTime(new Date());
		userService.insert(user);

		return R.ok();
	}
}
