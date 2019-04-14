package com.framework.modules.oss.service.impl;

import java.util.Map;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.framework.common.utils.PageUtils;
import com.framework.common.utils.Query;
import com.framework.modules.oss.dao.SysOssDao;
import com.framework.modules.oss.entity.SysOssEntity;
import com.framework.modules.oss.service.SysOssService;

@Service("sysOssService")
public class SysOssServiceImpl extends ServiceImpl<SysOssDao, SysOssEntity> implements SysOssService {

	@Override
	public PageUtils queryPage(Map<String, Object> params) {
		Page<SysOssEntity> page = this.selectPage(new Query<SysOssEntity>(params).getPage());

		return new PageUtils(page);
	}

}
