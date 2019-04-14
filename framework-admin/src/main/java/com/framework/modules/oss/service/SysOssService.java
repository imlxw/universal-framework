package com.framework.modules.oss.service;

import java.util.Map;

import com.baomidou.mybatisplus.service.IService;
import com.framework.common.utils.PageUtils;
import com.framework.modules.oss.entity.SysOssEntity;

/**
 * 文件上传
 */
public interface SysOssService extends IService<SysOssEntity> {

	PageUtils queryPage(Map<String, Object> params);
}
