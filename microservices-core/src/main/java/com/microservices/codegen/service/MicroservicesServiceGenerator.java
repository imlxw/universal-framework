package com.microservices.codegen.service;

import java.util.List;

import com.jfinal.plugin.activerecord.generator.TableMeta;
import com.microservices.Microservices;
import com.microservices.codegen.CodeGenHelpler;

public class MicroservicesServiceGenerator {

	public static void main(String[] args) {

		Microservices.setBootArg("microservices.datasource.url", "jdbc:mysql://127.0.0.1:3306/microservicesdemo");
		Microservices.setBootArg("microservices.datasource.user", "root");

		String basePackage = "com.microservices.codegen.service.test";
		String modelPackage = "com.microservices.codegen.model.test";
		run(basePackage, modelPackage);

	}

	public static void run(String basePackage, String modelPacket) {
		new MicroservicesServiceGenerator(basePackage, modelPacket).doGenerate(null);
	}

	public static void run(String basePackage, String modelPacket, String excludeTables) {
		new MicroservicesServiceGenerator(basePackage, modelPacket).doGenerate(excludeTables);
	}

	private String basePackage;
	private String modelPackage;

	public MicroservicesServiceGenerator(String basePackage, String modelPackage) {
		this.basePackage = basePackage;
		this.modelPackage = modelPackage;

	}

	public void doGenerate(String excludeTables) {

		System.out.println("start generate...");
		List<TableMeta> tableMetaList = CodeGenHelpler.createMetaBuilder().build();
		CodeGenHelpler.excludeTables(tableMetaList, excludeTables);

		new MicroservicesServiceInterfaceGenerator(basePackage, modelPackage).generate(tableMetaList);
		new MicroservicesServiceImplGenerator(basePackage, modelPackage).generate(tableMetaList);

		System.out.println("service generate finished !!!");

	}

}
