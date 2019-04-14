package com.framework.service;

import java.util.Map;

import com.baomidou.mybatisplus.service.IService;
import com.framework.entity.UserEntity;
import com.framework.form.LoginForm;

/**
 * 用户
 */
public interface UserService extends IService<UserEntity> {

	UserEntity queryByMobile(String mobile);

	/**
	 * 用户登录
	 * 
	 * @param form 登录表单
	 * @return 返回登录信息
	 */
	Map<String, Object> login(LoginForm form);
}
