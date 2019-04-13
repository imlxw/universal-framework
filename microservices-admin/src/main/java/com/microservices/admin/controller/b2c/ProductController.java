package com.microservices.admin.controller.b2c;

import com.jfinal.plugin.activerecord.Page;
import com.microservices.admin.base.common.RestResult;
import com.microservices.admin.base.common.ServiceConst;
import com.microservices.admin.base.exception.BusinessException;
import com.microservices.admin.base.interceptor.NotNullPara;
import com.microservices.admin.base.rest.datatable.DataTable;
import com.microservices.admin.base.web.base.BaseController;
import com.microservices.admin.support.auth.AuthUtils;
import com.microservices.b2c.service.api.ProductService;
import com.microservices.b2c.service.entity.model.Product;
import com.microservices.b2c.service.entity.status.ProductStatus;
import com.microservices.component.metric.annotation.EnableMetricCounter;
import com.microservices.component.metric.annotation.EnableMetricHistogram;
import com.microservices.component.metric.annotation.EnableMetricMeter;
import com.microservices.component.metric.annotation.EnableMetricTimer;
import com.microservices.core.rpc.annotation.MicroservicesrpcService;
import com.microservices.web.controller.annotation.RequestMapping;

import java.util.Date;

/**
 * 商品管理
 */
@RequestMapping("/b2c/product")
public class ProductController extends BaseController {

	@MicroservicesrpcService(group = ServiceConst.SERVICE_B2C, version = ServiceConst.VERSION_1_0)
	private ProductService productService;

	/**
	 * index
	 */
	@EnableMetricCounter("商品管理点击次数")
	@EnableMetricHistogram("商品管理点击分布")
	@EnableMetricMeter("商品管理点击频率")
	@EnableMetricTimer("商品管理计时")
	public void index() {
		render("main.html");
	}

	/**
	 * 表格数据
	 */
	public void tableData() {
		int pageNumber = getParaToInt("pageNumber", 1);
		int pageSize = getParaToInt("pageSize", 30);

		Product product = new Product();
		product.setName(getPara("name"));

		Page<Product> dataPage = productService.findPage(product, pageNumber, pageSize);
		renderJson(new DataTable<Product>(dataPage));
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
	@NotNullPara({ "product.name", "product.prodDesc", "product.totalNum" })
	public void postAdd() {
		Product product = getBean(Product.class, "product");

		product.setLastUpdAcct(AuthUtils.getLoginUser().getName());
		product.setStatus(ProductStatus.USED);
		product.setCreateDate(new Date());
		product.setLastUpdTime(new Date());
		product.setNote("新增商品");

		if (!productService.save(product)) {
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
		Product product = productService.findById(id);

		setAttr("product", product).render("update.html");
	}

	/**
	 * 修改提交
	 */
	@NotNullPara({ "product.id", "product.name", "product.prodDesc", "product.totalNum" })
	public void postUpdate() {
		Product product = getBean(Product.class, "product");

		if (productService.findById(product.getId()) == null) {
			throw new BusinessException("商品不存在");
		}

		product.setLastUpdAcct(AuthUtils.getLoginUser().getName());
		product.setStatus(ProductStatus.USED);
		product.setLastUpdTime(new Date());
		product.setNote("修改商品");

		if (!productService.update(product)) {
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
		if (!productService.deleteById(id)) {
			throw new BusinessException("删除失败");
		}

		renderJson(RestResult.buildSuccess());
	}
}
