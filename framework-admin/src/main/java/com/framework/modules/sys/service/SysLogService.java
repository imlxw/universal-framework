package com.framework.modules.sys.service;

import java.util.Map;

import com.baomidou.mybatisplus.service.IService;
import com.framework.common.utils.PageUtils;
import com.framework.modules.sys.entity.SysLogEntity;

/**
 * 系统日志
 */
public interface SysLogService extends IService<SysLogEntity> {

	PageUtils queryPage(Map<String, Object> params);

}
