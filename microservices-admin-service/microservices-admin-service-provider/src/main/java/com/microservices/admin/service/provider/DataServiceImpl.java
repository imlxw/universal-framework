package com.microservices.admin.service.provider;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Singleton;

import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Page;
import com.microservices.Microservices;
import com.microservices.admin.base.common.CacheKey;
import com.microservices.admin.service.api.DataService;
import com.microservices.admin.service.entity.model.Data;
import com.microservices.admin.service.entity.status.system.DataStatus;
import com.microservices.aop.annotation.Bean;
import com.microservices.core.cache.annotation.Cacheable;
import com.microservices.core.rpc.annotation.MicroservicesrpcService;
import com.microservices.db.model.Columns;
import com.microservices.service.MicroservicesServiceBase;

/**
 * 基础数据信息
 */
@Bean
@Singleton
@MicroservicesrpcService
public class DataServiceImpl extends MicroservicesServiceBase<Data> implements DataService {

	@Override
	public Page<Data> findPage(Data data, int pageNumber, int pageSize) {
		Columns columns = Columns.create();

		if (StrKit.notBlank(data.getType())) {
			columns.like("type", "%" + data.getType() + "%");
		}
		if (StrKit.notBlank(data.getTypeDesc())) {
			columns.like("typeDesc", "%" + data.getTypeDesc() + "%");
		}
		return DAO.paginateByColumns(pageNumber, pageSize, columns.getList(), "type asc ,orderNo asc");
	}

	@Cacheable(name = CacheKey.CACHE_KEYVALUE)
	@Override
	public String getCodeDescByCodeAndType(String code, String type) {
		Columns columns = Columns.create();
		columns.eq("type", type).eq("code", code);
		Data data = DAO.findFirstByColumns(columns);

		String codeDesc = "";
		if (data != null) {
			codeDesc = data.getCodeDesc();
		}
		return codeDesc;
	}

	@Cacheable(name = CacheKey.CACHE_KEYVALUE)
	@Override
	public String getCodeByCodeDescAndType(String type, String codeDesc) {
		Columns columns = Columns.create();
		columns.eq("type", type).eq("codeDesc", codeDesc);
		Data data = DAO.findFirstByColumns(columns);

		String code = "";
		if (data != null) {
			code = data.getCode();
		}
		return code;
	}

	@Cacheable(name = CacheKey.CACHE_KEYVALUE)
	@Override
	public Map<String, String> getMapByTypeOnUse(String type) {
		Columns columns = Columns.create();
		columns.eq("type", type).eq("status", DataStatus.USED);
		List<Data> dataList = DAO.findListByColumns(columns);

		Map<String, String> map = new LinkedHashMap<String, String>();
		for (Data data : dataList) {
			map.put(data.getCodeDesc(), data.getCode());
		}
		return map;
	}

	@Cacheable(name = CacheKey.CACHE_KEYVALUE)
	@Override
	public Map<String, String> getMapByType(String type) {
		Columns columns = Columns.create();
		columns.eq("type", type);
		List<Data> dataList = DAO.findListByColumns(columns);

		Map<String, String> map = new LinkedHashMap<String, String>();
		for (Data data : dataList) {
			map.put(data.getCodeDesc(), data.getCode());
		}
		return map;
	}

	@Cacheable(name = CacheKey.CACHE_KEYVALUE)
	@Override
	public List<Data> getListByTypeOnUse(String type) {
		Columns columns = Columns.create();
		columns.eq("type", type).eq("status", DataStatus.USED);
		return DAO.findListByColumns(columns);
	}

	@Cacheable(name = CacheKey.CACHE_KEYVALUE)
	@Override
	public List<Data> getListByType(String type) {
		Columns columns = Columns.create();
		columns.eq("type", type);
		return DAO.findListByColumns(columns);
	}

	@Override
	public void refreshCache() {
		Microservices.me().getCache().removeAll(CacheKey.CACHE_KEYVALUE);
	}
}