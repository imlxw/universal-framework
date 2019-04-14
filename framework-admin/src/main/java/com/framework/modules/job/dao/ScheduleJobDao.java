package com.framework.modules.job.dao;

import java.util.Map;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.framework.modules.job.entity.ScheduleJobEntity;

/**
 * 定时任务
 */
public interface ScheduleJobDao extends BaseMapper<ScheduleJobEntity> {

	/**
	 * 批量更新状态
	 */
	int updateBatch(Map<String, Object> map);
}
