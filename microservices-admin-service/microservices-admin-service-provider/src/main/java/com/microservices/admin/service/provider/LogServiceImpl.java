package com.microservices.admin.service.provider;

import javax.inject.Inject;
import javax.inject.Singleton;

import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Page;
import com.microservices.admin.service.api.LogService;
import com.microservices.admin.service.api.UserService;
import com.microservices.admin.service.entity.model.Log;
import com.microservices.aop.annotation.Bean;
import com.microservices.core.rpc.annotation.MicroservicesrpcService;
import com.microservices.db.model.Columns;
import com.microservices.service.MicroservicesServiceBase;

/**
 * 日志信息
 */
@Bean
@Singleton
@MicroservicesrpcService
public class LogServiceImpl extends MicroservicesServiceBase<Log> implements LogService {

	@Inject
	private UserService userService;

	@Override
	public Page<Log> findPage(Log log, int pageNumber, int pageSize) {
		Columns columns = Columns.create();

		if (StrKit.notBlank(log.getIp())) {
			columns.like("ip", "%" + log.getIp() + "%");
		}
		if (StrKit.notBlank(log.getUrl())) {
			columns.like("url", "%" + log.getUrl() + "%");
		}
		if (StrKit.notBlank(log.getLastUpdAcct())) {
			columns.like("lastUpdAcct", "%" + log.getLastUpdAcct() + "%");
		}

		return DAO.paginateByColumns(pageNumber, pageSize, columns.getList(), "id desc");
	}
}