package com.framework.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.framework.datasources.DataSourceNames;
import com.framework.datasources.annotation.DataSource;
import com.framework.modules.sys.entity.SysUserEntity;
import com.framework.modules.sys.service.SysUserService;

/**
 * 测试多数据源
 */
@Service
public class DataSourceTestService {
	@Autowired
	private SysUserService sysUserService;

	public SysUserEntity queryUser(Long userId) {
		return sysUserService.selectById(userId);
	}

	@DataSource(name = DataSourceNames.SECOND)
	public SysUserEntity queryUser2(Long userId) {
		return sysUserService.selectById(userId);
	}
}
