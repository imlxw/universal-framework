package com.microservices.admin.controller.system;

import com.jfinal.plugin.activerecord.Page;
import com.microservices.admin.base.common.RestResult;
import com.microservices.admin.base.exception.BusinessException;
import com.microservices.admin.base.interceptor.NotNullPara;
import com.microservices.admin.base.rest.datatable.DataTable;
import com.microservices.admin.base.web.base.BaseController;
import com.microservices.admin.service.api.DataService;
import com.microservices.admin.service.entity.model.Data;
import com.microservices.admin.service.entity.status.system.DataStatus;
import com.microservices.admin.support.auth.AuthUtils;
import com.microservices.core.rpc.annotation.MicroservicesrpcService;
import com.microservices.web.controller.annotation.RequestMapping;

import java.util.Date;

/**
 * 数据字典管理
 */
@RequestMapping("/system/data")
public class DataController extends BaseController {

	@MicroservicesrpcService
	private DataService dataService;

	/**
	 * index
	 */
	public void index() {
		render("main.html");
	}

	/**
	 * 表格数据
	 */
	public void tableData() {
		int pageNumber = getParaToInt("pageNumber", 1);
		int pageSize = getParaToInt("pageSize", 30);

		Data data = new Data();
		data.setType(getPara("type"));
		data.setTypeDesc(getPara("typeDesc"));

		Page<Data> dataPage = dataService.findPage(data, pageNumber, pageSize);
		renderJson(new DataTable<Data>(dataPage));
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
		Data data = getBean(Data.class, "data");

		data.setLastUpdAcct(AuthUtils.getLoginUser().getName());
		data.setStatus(DataStatus.USED);
		data.setCreateDate(new Date());
		data.setLastUpdTime(new Date());
		data.setNote("保存数据字典");

		if (!dataService.save(data)) {
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
		Data data = dataService.findById(id);

		setAttr("data", data).render("update.html");
	}

	/**
	 * 修改提交
	 */
	public void postUpdate() {
		Data data = getBean(Data.class, "data");

		if (dataService.findById(data.getId()) == null) {
			throw new BusinessException("数据不存在");
		}

		data.setLastUpdAcct(AuthUtils.getLoginUser().getName());
		data.setStatus(DataStatus.USED);
		data.setLastUpdTime(new Date());
		data.setNote("修改数据字典");

		if (!dataService.update(data)) {
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
		if (!dataService.deleteById(id)) {
			throw new BusinessException("删除失败");
		}

		renderJson(RestResult.buildSuccess());
	}

	/**
	 * 启用资源
	 */
	@NotNullPara({ "id" })
	public void use() {
		Long id = getParaToLong("id");

		Data data = dataService.findById(id);
		if (data == null) {
			throw new BusinessException("编号为" + id + "的数据不存在");
		}
		data.setStatus(DataStatus.USED);
		data.setLastUpdTime(new Date());
		data.setNote("启用数据");

		if (!dataService.update(data)) {
			throw new BusinessException("启用失败");
		}

		renderJson(RestResult.buildSuccess());
	}

	/**
	 * 停用资源
	 */
	@NotNullPara({ "id" })
	public void unuse() {
		Long id = getParaToLong("id");
		Data data = dataService.findById(id);
		if (data == null) {
			throw new BusinessException("编号为" + id + "的数据不存在");
		}

		data.setStatus(DataStatus.UNUSED);
		data.setLastUpdTime(new Date());
		data.setNote("停用数据");
		data.update();

		if (!dataService.update(data)) {
			throw new BusinessException("停用失败");
		}

		renderJson(RestResult.buildSuccess());
	}

	public void cache() {
		dataService.refreshCache();
		renderJson(RestResult.buildSuccess());
	}

}
