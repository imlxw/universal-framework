package com.microservices.admin.base.gencode.serviceimpl;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import com.jfinal.kit.Kv;
import com.jfinal.kit.PathKit;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.generator.BaseModelGenerator;
import com.jfinal.plugin.activerecord.generator.TableMeta;
import com.jfinal.template.Engine;
import com.jfinal.template.source.ClassPathSourceFactory;

/**
 * 代码生成实现
 */
public class AppMicroservicesServiceImplGenerator extends BaseModelGenerator {

	private String modelPacket;
	private String servicePackage;
	private String serviceImplPackage;

	public AppMicroservicesServiceImplGenerator(String servicePackage, String modelPacket, String serviceImplPackage) {
		super(serviceImplPackage, PathKit.getWebRootPath() + "/src/main/java/" + serviceImplPackage.replace(".", "/"));

		this.modelPacket = modelPacket;
		this.servicePackage = servicePackage;
		this.serviceImplPackage = serviceImplPackage;
		this.template = "com/microservices/codegen/service/service_impl_template.jf";

	}

	@Override
	public void generate(List<TableMeta> tableMetas) {
		System.out.println("Generate base model ...");
		System.out.println("Base Model Output Dir: " + baseModelOutputDir);

		Engine engine = Engine.create("forServiceImpl");
		engine.setSourceFactory(new ClassPathSourceFactory());
		engine.addSharedMethod(new StrKit());
		engine.addSharedObject("getterTypeMap", getterTypeMap);
		engine.addSharedObject("javaKeyword", javaKeyword);

		for (TableMeta tableMeta : tableMetas) {
			genBaseModelContent(tableMeta);
		}
		writeToFile(tableMetas);
	}

	@Override
	protected void genBaseModelContent(TableMeta tableMeta) {
		Kv data = Kv.by("serviceImplPackageName", baseModelPackageName);
		data.set("generateChainSetter", generateChainSetter);
		data.set("tableMeta", tableMeta);
		data.set("basePackage", servicePackage);
		data.set("modelPackage", modelPacket);

		Engine engine = Engine.use("forServiceImpl");
		tableMeta.baseModelContent = engine.getTemplate(template).renderToString(data);
	}

	/**
	 * base model 覆盖写入
	 */
	@Override
	protected void writeToFile(TableMeta tableMeta) throws IOException {
		File dir = new File(baseModelOutputDir);
		if (!dir.exists()) {
			dir.mkdirs();
		}

		String target = baseModelOutputDir + File.separator + tableMeta.modelName + "ServiceImpl" + ".java";

		File targetFile = new File(target);
		if (targetFile.exists()) {
			return;
		}

		FileWriter fw = new FileWriter(target);
		try {
			fw.write(tableMeta.baseModelContent);
		} finally {
			fw.close();
		}
	}
}
