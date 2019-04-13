package com.microservices.admin.service.provider;

import java.util.List;

import javax.inject.Singleton;

import com.jfinal.plugin.activerecord.Db;
import com.microservices.admin.service.api.UserRoleService;
import com.microservices.admin.service.entity.model.UserRole;
import com.microservices.aop.annotation.Bean;
import com.microservices.core.rpc.annotation.MicroservicesrpcService;
import com.microservices.service.MicroservicesServiceBase;

/**
 * 用户角色信息
 */
@Bean
@Singleton
@MicroservicesrpcService
public class UserRoleServiceImpl extends MicroservicesServiceBase<UserRole> implements UserRoleService {

	@Override
	public int deleteByUserId(Long userId) {
		return Db.update("delete from sys_user_role where user_id = ?", userId);
	}

	@Override
	public int[] batchSave(List<UserRole> list) {
		return Db.batchSave(list, list.size());
	}
}