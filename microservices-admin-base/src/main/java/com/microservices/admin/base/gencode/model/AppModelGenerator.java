package com.microservices.admin.base.gencode.model;

import com.jfinal.kit.PathKit;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.generator.TableMeta;
import com.microservices.Microservices;
import com.microservices.codegen.CodeGenHelpler;
import com.microservices.codegen.model.MicroservicesBaseModelGenerator;
import com.microservices.codegen.model.MicroservicesModelnfoGenerator;

import javax.sql.DataSource;
import java.util.List;

/**
 * model代码自动生成
 */
public class AppModelGenerator {

	public static void doGenerate() {
		AppModelGeneratorConfig config = Microservices.config(AppModelGeneratorConfig.class);

		System.out.println(config.toString());

		if (StrKit.isBlank(config.getModelpackage())) {
			System.err.println("microservices.admin.model.ge.modelpackage 不可为空");
			System.exit(0);
		}

		String modelPackage = config.getModelpackage();
		String baseModelPackage = modelPackage + ".base";

		String modelDir = PathKit.getWebRootPath() + "/src/main/java/" + modelPackage.replace(".", "/");
		String baseModelDir = PathKit.getWebRootPath() + "/src/main/java/" + baseModelPackage.replace(".", "/");

		System.out.println("start generate...");
		System.out.println("generate dir:" + modelDir);

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

		new MicroservicesBaseModelGenerator(baseModelPackage, baseModelDir).generate(tableMetaList);
		new MicroservicesModelnfoGenerator(modelPackage, baseModelPackage, modelDir).generate(tableMetaList);

		System.out.println("entity generate finished !!!");

	}

}
