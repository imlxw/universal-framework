package com.framework.modules.sys.service;

import java.util.List;
import java.util.Map;

import com.baomidou.mybatisplus.service.IService;
import com.framework.common.utils.PageUtils;
import com.framework.modules.sys.entity.SysUserEntity;

/**
 * 系统用户
 */
public interface SysUserService extends IService<SysUserEntity> {

	PageUtils queryPage(Map<String, Object> params);

	/**
	 * 查询用户的所有菜单ID
	 */
	List<Long> queryAllMenuId(Long userId);

	/**
	 * 保存用户
	 */
	void save(SysUserEntity user);

	/**
	 * 修改用户
	 */
	void update(SysUserEntity user);

	/**
	 * 修改密码
	 * 
	 * @param userId 用户ID
	 * @param password 原密码
	 * @param newPassword 新密码
	 */
	boolean updatePassword(Long userId, String password, String newPassword);
}
