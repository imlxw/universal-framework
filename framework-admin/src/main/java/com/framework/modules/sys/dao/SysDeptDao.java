package com.framework.modules.sys.dao;

import java.util.List;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.framework.modules.sys.entity.SysDeptEntity;

/**
 * 部门管理
 */
public interface SysDeptDao extends BaseMapper<SysDeptEntity> {

	/**
	 * 查询子部门ID列表
	 * 
	 * @param parentId 上级部门ID
	 */
	List<Long> queryDetpIdList(Long parentId);

}
