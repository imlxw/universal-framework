package com.framework.modules.job.service;

import java.util.Map;

import com.baomidou.mybatisplus.service.IService;
import com.framework.common.utils.PageUtils;
import com.framework.modules.job.entity.ScheduleJobLogEntity;

/**
 * 定时任务日志
 */
public interface ScheduleJobLogService extends IService<ScheduleJobLogEntity> {

	PageUtils queryPage(Map<String, Object> params);

}
