package com.microservices.codegen.model;

import java.util.List;

import com.jfinal.kit.PathKit;
import com.jfinal.plugin.activerecord.generator.TableMeta;
import com.microservices.Microservices;
import com.microservices.codegen.CodeGenHelpler;

public class MicroservicesModelGenerator {

	public static void main(String[] args) {

		Microservices.setBootArg("microservices.datasource.url", "jdbc:mysql://127.0.0.1:3306/microservicesdemo");
		Microservices.setBootArg("microservices.datasource.user", "root");

		String modelPackage = "com.microservices.codegen.model.test";
		run(modelPackage);
	}

	public static void run(String modelPackage) {
		new MicroservicesModelGenerator(modelPackage).doGenerate(null);
	}

	public static void run(String modelPackage, String excludeTables) {
		new MicroservicesModelGenerator(modelPackage).doGenerate(excludeTables);
	}

	private final String basePackage;

	public MicroservicesModelGenerator(String basePackage) {
		this.basePackage = basePackage;
	}

	public void doGenerate(String excludeTables) {

		String modelPackage = basePackage;
		String baseModelPackage = basePackage + ".base";

		String modelDir = PathKit.getWebRootPath() + "/src/main/java/" + modelPackage.replace(".", "/");
		String baseModelDir = PathKit.getWebRootPath() + "/src/main/java/" + baseModelPackage.replace(".", "/");

		System.out.println("start generate...");
		System.out.println("generate dir:" + modelDir);

		List<TableMeta> tableMetaList = CodeGenHelpler.createMetaBuilder().build();
		CodeGenHelpler.excludeTables(tableMetaList, excludeTables);

		new MicroservicesBaseModelGenerator(baseModelPackage, baseModelDir).generate(tableMetaList);
		new MicroservicesModelnfoGenerator(modelPackage, baseModelPackage, modelDir).generate(tableMetaList);

		System.out.println("model generate finished !!!");

	}

}
