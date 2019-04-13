package com.microservices.admin.service.provider;

import java.util.List;

import javax.inject.Singleton;

import com.jfinal.plugin.activerecord.Db;
import com.microservices.admin.service.api.RoleResService;
import com.microservices.admin.service.entity.model.RoleRes;
import com.microservices.aop.annotation.Bean;
import com.microservices.core.rpc.annotation.MicroservicesrpcService;
import com.microservices.service.MicroservicesServiceBase;

/**
 * 角色资源信息
 */
@Bean
@Singleton
@MicroservicesrpcService
public class RoleResServiceImpl extends MicroservicesServiceBase<RoleRes> implements RoleResService {

	@Override
	public int deleteByRoleId(Long roleId) {
		return Db.update("delete from sys_role_res where role_id = ?", roleId);
	}

	@Override
	public int[] batchSave(List<RoleRes> list) {
		return Db.batchSave(list, list.size());
	}
}