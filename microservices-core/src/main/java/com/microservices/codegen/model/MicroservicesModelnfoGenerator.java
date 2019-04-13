package com.microservices.codegen.model;

import com.jfinal.plugin.activerecord.generator.ModelGenerator;

public class MicroservicesModelnfoGenerator extends ModelGenerator {

	public MicroservicesModelnfoGenerator(String modelPackageName, String baseModelPackageName, String modelOutputDir) {
		super(modelPackageName, baseModelPackageName, modelOutputDir);

		this.template = "/com/microservices/codegen/model/model_template.jf";

	}

}
