package com.microservices.b2c.service.provider;

import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Page;
import com.microservices.aop.annotation.Bean;
import com.microservices.b2c.service.api.ProductService;
import com.microservices.b2c.service.entity.model.Product;
import com.microservices.core.rpc.annotation.MicroservicesrpcService;
import com.microservices.db.model.Columns;
import com.microservices.service.MicroservicesServiceBase;

import javax.inject.Singleton;

@Bean
@Singleton
@MicroservicesrpcService
public class ProductServiceImpl extends MicroservicesServiceBase<Product> implements ProductService {

	@Override
	public Page<Product> findPage(Product product, int pageNumber, int pageSize) {
		Columns columns = Columns.create();

		if (StrKit.notBlank(product.getName())) {
			columns.like("name", "%" + product.getName() + "%");
		}
		return DAO.paginateByColumns(pageNumber, pageSize, columns.getList(), "id desc");
	}
}