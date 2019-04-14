package com.framework.modules.sys.service.impl;

import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.framework.common.utils.PageUtils;
import com.framework.common.utils.Query;
import com.framework.modules.sys.dao.SysDictDao;
import com.framework.modules.sys.entity.SysDictEntity;
import com.framework.modules.sys.service.SysDictService;

@Service("sysDictService")
public class SysDictServiceImpl extends ServiceImpl<SysDictDao, SysDictEntity> implements SysDictService {

	@Override
	public PageUtils queryPage(Map<String, Object> params) {
		String name = (String) params.get("name");

		Page<SysDictEntity> page = this.selectPage(new Query<SysDictEntity>(params).getPage(), new EntityWrapper<SysDictEntity>().like(StringUtils.isNotBlank(name), "name", name));

		return new PageUtils(page);
	}

}
