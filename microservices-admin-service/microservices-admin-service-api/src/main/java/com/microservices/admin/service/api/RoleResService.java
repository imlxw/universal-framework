package com.microservices.admin.service.api;

import java.util.List;

import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.Page;
import com.microservices.admin.service.entity.model.RoleRes;

/**
 * 角色资源信息
 */
public interface RoleResService {

	/**
	 * 根据roleId删除model
	 * 
	 * @param roleId
	 */
	public int deleteByRoleId(Long roleId);

	/**
	 * 批量保存 model
	 * 
	 * @param list
	 * @return
	 */
	public int[] batchSave(List<RoleRes> list);

	/**
	 * 根据ID查找model
	 *
	 * @param id
	 * @return
	 */
	public RoleRes findById(Object id);

	/**
	 * 根据ID删除model
	 *
	 * @param id
	 * @return
	 */
	public boolean deleteById(Object id);

	/**
	 * 删除
	 *
	 * @param model
	 * @return
	 */
	public boolean delete(RoleRes model);

	/**
	 * 保存到数据库
	 *
	 * @param model
	 * @return
	 */
	public boolean save(RoleRes model);

	/**
	 * 保存或更新
	 *
	 * @param model
	 * @return
	 */
	public boolean saveOrUpdate(RoleRes model);

	/**
	 * 更新 model
	 *
	 * @param model
	 * @return
	 */
	public boolean update(RoleRes model);

	public void join(Page<? extends Model> page, String joinOnField);

	public void join(Page<? extends Model> page, String joinOnField, String[] attrs);

	public void join(Page<? extends Model> page, String joinOnField, String joinName);

	public void join(Page<? extends Model> page, String joinOnField, String joinName, String[] attrs);

	public void join(List<? extends Model> models, String joinOnField);

	public void join(List<? extends Model> models, String joinOnField, String[] attrs);

	public void join(List<? extends Model> models, String joinOnField, String joinName);

	public void join(List<? extends Model> models, String joinOnField, String joinName, String[] attrs);

	/**
	 * 添加关联数据到某个model中去，避免关联查询，提高性能。
	 *
	 * @param model 要添加到的model
	 * @param joinOnField model对于的关联字段
	 */
	public void join(Model model, String joinOnField);

	/**
	 * 添加关联数据到某个model中去，避免关联查询，提高性能。
	 *
	 * @param model
	 * @param joinOnField
	 * @param attrs
	 */
	public void join(Model model, String joinOnField, String[] attrs);

	/**
	 * 添加关联数据到某个model中去，避免关联查询，提高性能。
	 *
	 * @param model
	 * @param joinOnField
	 * @param joinName
	 */
	public void join(Model model, String joinOnField, String joinName);

	/**
	 * 添加关联数据到某个model中去，避免关联查询，提高性能。
	 *
	 * @param model
	 * @param joinOnField
	 * @param joinName
	 * @param attrs
	 */
	public void join(Model model, String joinOnField, String joinName, String[] attrs);

	public void keep(Model model, String... attrs);

	public void keep(List<? extends Model> models, String... attrs);
}