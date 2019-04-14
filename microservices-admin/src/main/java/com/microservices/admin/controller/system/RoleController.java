package com.microservices.admin.controller.system;

import com.jfinal.plugin.activerecord.Page;
import com.microservices.admin.base.common.RestResult;
import com.microservices.admin.base.exception.BusinessException;
import com.microservices.admin.base.interceptor.NotNullPara;
import com.microservices.admin.base.plugin.shiro.ShiroCacheUtils;
import com.microservices.admin.base.rest.datatable.DataTable;
import com.microservices.admin.base.web.base.BaseController;
import com.microservices.admin.service.api.RoleService;
import com.microservices.admin.service.entity.model.Role;
import com.microservices.admin.service.entity.status.system.RoleStatus;
import com.microservices.admin.support.auth.AuthUtils;
import com.microservices.core.rpc.annotation.MicroservicesrpcService;
import com.microservices.web.controller.annotation.RequestMapping;

import java.util.Date;

/**
 * 系统角色管理
 */
@RequestMapping("/system/role")
public class RoleController extends BaseController {

	@MicroservicesrpcService
	private RoleService roleService;

	/**
	 * index
	 */
	public void index() {
		render("main.html");
	}

	/**
	 * res表格数据
	 */
	public void tableData() {
		int pageNumber = getParaToInt("pageNumber", 1);
		int pageSize = getParaToInt("pageSize", 30);

		Role sysRole = new Role();
		sysRole.setName(getPara("name"));

		Page<Role> rolePage = roleService.findPage(sysRole, pageNumber, pageSize);
		renderJson(new DataTable<Role>(rolePage));
	}

	/**
	 * add
	 */
	public void add() {
		render("add.html");
	}

	/**
	 * 保存提交
	 */
	public void postAdd() {
		Role sysRole = getBean(Role.class, "role");

		sysRole.setLastUpdAcct(AuthUtils.getLoginUser().getName());
		sysRole.setStatus(RoleStatus.USED);
		sysRole.setLastUpdTime(new Date());
		sysRole.setNote("保存系统角色");

		if (!roleService.save(sysRole)) {
			throw new BusinessException("保存失败");
		}

		renderJson(RestResult.buildSuccess());
	}

	/**
	 * update
	 */
	@NotNullPara({ "id" })
	public void update() {
		Long id = getParaToLong("id");

		Role sysRole = roleService.findById(id);
		setAttr("role", sysRole).render("update.html");
	}

	/**
	 * 修改提交
	 */
	public void postUpdate() {
		Role sysRole = getBean(Role.class, "role");

		sysRole.setLastUpdAcct(AuthUtils.getLoginUser().getName());
		sysRole.setLastUpdTime(new Date());
		sysRole.setNote("修改系统资源");

		if (!roleService.update(sysRole)) {
			throw new BusinessException("修改失败");
		}

		renderJson(RestResult.buildSuccess());
	}

	/**
	 * 删除
	 */
	@NotNullPara({ "id" })
	public void delete() {
		Long id = getParaToLong("id");
		if (!roleService.deleteById(id)) {
			throw new BusinessException("删除失败");
		}

		renderJson(RestResult.buildSuccess());
	}

	/**
	 * 角色赋权
	 */
	@NotNullPara({ "id" })
	public void auth() {
		Role sysRole = roleService.findById(getParaToLong("id"));
		setAttr("role", sysRole).render("auth.html");
	}

	/**
	 * 角色赋权提交
	 */
	@NotNullPara({ "id", "resIds" })
	public void postAuth() {
		String resIds = getPara("resIds");
		Long id = getParaToLong("id");

		if (!roleService.auth(id, resIds)) {
			throw new BusinessException("赋权失败");
		}

		ShiroCacheUtils.clearAuthorizationInfoAll();
		renderJson(RestResult.buildSuccess());
	}
}
