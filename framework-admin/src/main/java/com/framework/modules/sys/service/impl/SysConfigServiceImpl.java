package com.framework.modules.sys.service.impl;

import java.util.Arrays;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.framework.common.exception.RRException;
import com.framework.common.utils.PageUtils;
import com.framework.common.utils.Query;
import com.framework.modules.sys.dao.SysConfigDao;
import com.framework.modules.sys.entity.SysConfigEntity;
import com.framework.modules.sys.redis.SysConfigRedis;
import com.framework.modules.sys.service.SysConfigService;
import com.google.gson.Gson;

@Service("sysConfigService")
public class SysConfigServiceImpl extends ServiceImpl<SysConfigDao, SysConfigEntity> implements SysConfigService {
	@Autowired
	private SysConfigRedis sysConfigRedis;

	@Override
	public PageUtils queryPage(Map<String, Object> params) {
		String paramKey = (String) params.get("paramKey");

		Page<SysConfigEntity> page = this.selectPage(new Query<SysConfigEntity>(params).getPage(), new EntityWrapper<SysConfigEntity>().like(StringUtils.isNotBlank(paramKey), "param_key", paramKey).eq("status", 1));

		return new PageUtils(page);
	}

	@Override
	public void save(SysConfigEntity config) {
		this.insert(config);
		sysConfigRedis.saveOrUpdate(config);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void update(SysConfigEntity config) {
		this.updateAllColumnById(config);
		sysConfigRedis.saveOrUpdate(config);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void updateValueByKey(String key, String value) {
		baseMapper.updateValueByKey(key, value);
		sysConfigRedis.delete(key);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void deleteBatch(Long[] ids) {
		for (Long id : ids) {
			SysConfigEntity config = this.selectById(id);
			sysConfigRedis.delete(config.getParamKey());
		}

		this.deleteBatchIds(Arrays.asList(ids));
	}

	@Override
	public String getValue(String key) {
		SysConfigEntity config = sysConfigRedis.get(key);
		if (config == null) {
			config = baseMapper.queryByKey(key);
			sysConfigRedis.saveOrUpdate(config);
		}

		return config == null ? null : config.getParamValue();
	}

	@Override
	public <T> T getConfigObject(String key, Class<T> clazz) {
		String value = getValue(key);
		if (StringUtils.isNotBlank(value)) {
			return new Gson().fromJson(value, clazz);
		}

		try {
			return clazz.newInstance();
		} catch (Exception e) {
			throw new RRException("获取参数失败");
		}
	}
}
