package com.framework.modules.sys.dao;

import java.util.List;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.framework.modules.sys.entity.SysMenuEntity;

/**
 * 菜单管理
 */
public interface SysMenuDao extends BaseMapper<SysMenuEntity> {

	/**
	 * 根据父菜单，查询子菜单
	 * 
	 * @param parentId 父菜单ID
	 */
	List<SysMenuEntity> queryListParentId(Long parentId);

	/**
	 * 获取不包含按钮的菜单列表
	 */
	List<SysMenuEntity> queryNotButtonList();

}
