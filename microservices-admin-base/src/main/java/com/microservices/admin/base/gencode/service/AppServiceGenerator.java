package com.microservices.admin.base.gencode.service;

import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.generator.TableMeta;
import com.microservices.Microservices;
import com.microservices.admin.base.gencode.model.AppMetaBuilder;
import com.microservices.codegen.CodeGenHelpler;
import com.microservices.codegen.service.MicroservicesServiceInterfaceGenerator;

import javax.sql.DataSource;
import java.util.List;

/**
 * service 生成
 */
public class AppServiceGenerator {

	public static void doGenerate() {
		AppServiceGeneratorConfig config = Microservices.config(AppServiceGeneratorConfig.class);

		System.out.println(config.toString());

		if (StrKit.isBlank(config.getModelpackage())) {
			System.err.println("microservices.admin.service.ge.modelpackage 不可为空");
			System.exit(0);
		}

		if (StrKit.isBlank(config.getServicepackage())) {
			System.err.println("microservices.admin.service.ge.servicepackage 不可为空");
			System.exit(0);
		}

		String modelPackage = config.getModelpackage();
		String servicepackage = config.getServicepackage();

		System.out.println("start generate...");
		System.out.println("generate dir:" + servicepackage);

		DataSource dataSource = CodeGenHelpler.getDatasource();

		AppMetaBuilder metaBuilder = new AppMetaBuilder(dataSource);

		if (StrKit.notBlank(config.getRemovedtablenameprefixes())) {
			metaBuilder.setRemovedTableNamePrefixes(config.getRemovedtablenameprefixes().split(","));
		}

		if (StrKit.notBlank(config.getExcludedtableprefixes())) {
			metaBuilder.setSkipPre(config.getExcludedtableprefixes().split(","));
		}

		List<TableMeta> tableMetaList = metaBuilder.build();
		CodeGenHelpler.excludeTables(tableMetaList, config.getExcludedtable());

		new MicroservicesServiceInterfaceGenerator(servicepackage, modelPackage).generate(tableMetaList);

		System.out.println("service generate finished !!!");

	}
}
