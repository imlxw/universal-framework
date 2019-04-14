package com.framework.service.impl;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.framework.common.exception.RRException;
import com.framework.common.validator.Assert;
import com.framework.dao.UserDao;
import com.framework.entity.TokenEntity;
import com.framework.entity.UserEntity;
import com.framework.form.LoginForm;
import com.framework.service.TokenService;
import com.framework.service.UserService;

@Service("userService")
public class UserServiceImpl extends ServiceImpl<UserDao, UserEntity> implements UserService {
	@Autowired
	private TokenService tokenService;

	@Override
	public UserEntity queryByMobile(String mobile) {
		UserEntity userEntity = new UserEntity();
		userEntity.setMobile(mobile);
		return baseMapper.selectOne(userEntity);
	}

	@Override
	public Map<String, Object> login(LoginForm form) {
		UserEntity user = queryByMobile(form.getMobile());
		Assert.isNull(user, "手机号或密码错误");

		// 密码错误
		if (!user.getPassword().equals(DigestUtils.sha256Hex(form.getPassword()))) {
			throw new RRException("手机号或密码错误");
		}

		// 获取登录token
		TokenEntity tokenEntity = tokenService.createToken(user.getUserId());

		Map<String, Object> map = new HashMap<>(2);
		map.put("token", tokenEntity.getToken());
		map.put("expire", tokenEntity.getExpireTime().getTime() - System.currentTimeMillis());

		return map;
	}

}
