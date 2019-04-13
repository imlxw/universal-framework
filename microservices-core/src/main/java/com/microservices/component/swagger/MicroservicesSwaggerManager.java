package com.microservices.component.swagger;

import static io.swagger.models.Scheme.HTTP;
import static io.swagger.models.Scheme.HTTPS;

import java.util.List;

import com.microservices.Microservices;
import com.microservices.utils.ClassScanner;
import com.microservices.web.controller.annotation.RequestMapping;

import io.swagger.models.Contact;
import io.swagger.models.Info;
import io.swagger.models.License;
import io.swagger.models.Swagger;

/**
 * @version V1.0
 * @Package com.microservices.component.swagger
 *          <p>
 */
public class MicroservicesSwaggerManager {

	private MicroservicesSwaggerConfig config = Microservices.config(MicroservicesSwaggerConfig.class);
	private Swagger swagger;
	private static MicroservicesSwaggerManager instance;

	public static MicroservicesSwaggerManager me() {
		if (instance == null) {
			instance = new MicroservicesSwaggerManager();
		}

		return instance;
	}

	public void init() {
		if (!config.isConfigOk()) {
			return;
		}

		swagger = new Swagger();
		swagger.setHost(config.getHost());
		swagger.setBasePath("/");
		swagger.addScheme(HTTP);
		swagger.addScheme(HTTPS);

		Info swaggerInfo = new Info();
		swaggerInfo.setDescription(config.getDescription());
		swaggerInfo.setVersion(config.getVersion());
		swaggerInfo.setTitle(config.getTitle());
		swaggerInfo.setTermsOfService(config.getTermsOfService());

		Contact contact = new Contact();
		contact.setName(config.getContactName());
		contact.setEmail(config.getContactEmail());
		contact.setUrl(config.getContactUrl());
		swaggerInfo.setContact(contact);

		License license = new License();
		license.setName(config.getLicenseName());
		license.setUrl(config.getLicenseUrl());
		swaggerInfo.setLicense(license);

		swagger.setInfo(swaggerInfo);

		List<Class> classes = ClassScanner.scanClassByAnnotation(RequestMapping.class, false);

		Reader.read(swagger, classes);

	}

	public Swagger getSwagger() {
		return swagger;
	}
}
