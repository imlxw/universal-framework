package com.microservices.admin.controller.system;

import com.jfinal.plugin.activerecord.Page;
import com.microservices.admin.base.rest.datatable.DataTable;
import com.microservices.admin.base.web.base.BaseController;
import com.microservices.admin.service.api.LogService;
import com.microservices.admin.service.entity.model.Log;
import com.microservices.core.rpc.annotation.MicroservicesrpcService;
import com.microservices.web.controller.annotation.RequestMapping;

/**
 * 日志管理
 */
@RequestMapping("/system/log")
public class LogController extends BaseController {

	@MicroservicesrpcService
	private LogService logService;

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

		Log log = new Log();
		log.setIp(getPara("ip"));
		log.setUrl(getPara("url"));
		log.setLastUpdAcct(getPara("userName"));

		Page<Log> logPage = logService.findPage(log, pageNumber, pageSize);
		renderJson(new DataTable<Log>(logPage));
	}

}
