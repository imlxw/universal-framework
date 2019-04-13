package com.microservices.component.swagger;

import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.annotation.JSONField;

import io.swagger.models.HttpMethod;
import io.swagger.models.Operation;
import io.swagger.models.Path;

/**
 * @version V1.0
 * @Title: 自定义 Swagger Path
 * @Description: 目的是为了 防止 fastjson 生成 opreations 和 operationMap 的json生成
 * @Package com.microservices.component.swagger
 */
public class SwaggerPath extends Path {

	@Override
	@JSONField(serialize = false)
	public List<Operation> getOperations() {
		return super.getOperations();
	}

	@Override
	@JSONField(serialize = false)
	public Map<HttpMethod, Operation> getOperationMap() {
		return super.getOperationMap();
	}
}
