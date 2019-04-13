package com.microservices.codegen.model;

import com.jfinal.plugin.activerecord.generator.BaseModelGenerator;

public class MicroservicesBaseModelGenerator extends BaseModelGenerator {

	public MicroservicesBaseModelGenerator(String baseModelPackageName, String baseModelOutputDir) {
		super(baseModelPackageName, baseModelOutputDir);

		this.template = "/com/microservices/codegen/model/base_model_template.jf";

	}

}
