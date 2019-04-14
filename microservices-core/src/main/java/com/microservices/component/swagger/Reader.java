package com.microservices.component.swagger;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.jfinal.core.Controller;
import com.microservices.web.MicroservicesControllerManager;

import io.swagger.models.Operation;
import io.swagger.models.Path;
import io.swagger.models.Response;
import io.swagger.models.Swagger;
import io.swagger.util.PathUtils;
import io.swagger.util.ReflectionUtils;

/**
 * 参考 ： https://github.com/swagger-api/swagger-core 的 servlet 模块
 */
public class Reader {

	private final Swagger swagger;

	private Reader(Swagger swagger) {
		this.swagger = swagger;
	}

	/**
	 * Scans a set of classes for Swagger annotations.
	 *
	 * @param swagger is the Swagger instance
	 * @param classes are a set of classes to scan
	 */
	public static void read(Swagger swagger, List<Class> classes) {
		final Reader reader = new Reader(swagger);
		for (Class cls : classes) {
			ReaderContext context = new ReaderContext(cls, "", null, false);
			reader.read(context);
		}
	}

	private void read(ReaderContext context) {

		for (Method method : context.getCls().getDeclaredMethods()) {
			if (ReflectionUtils.isOverriddenMethod(method, context.getCls())) {
				continue;
			}
			final Operation operation = new Operation();

			final Type[] genericParameterTypes = method.getGenericParameterTypes();
			final Annotation[][] paramAnnotations = method.getParameterAnnotations();

			ControllerReaderExtension extension = new ControllerReaderExtension();

			String methodPath = "index".equals(method.getName()) ? "" : "/" + method.getName();
			String operationPath = MicroservicesControllerManager.me().getPathByController((Class<? extends Controller>) context.getCls()) + methodPath;

			String httpMethod = extension.getHttpMethod(context, method);

			if (operationPath == null || httpMethod == null) {
				continue;
			}

			if (extension.isReadable(context)) {
				extension.setDeprecated(operation, method);
				extension.applyConsumes(context, operation, method);
				extension.applyProduces(context, operation, method);
				extension.applyOperationId(operation, method);
				extension.applySummary(operation, method);
				extension.applyDescription(operation, method);
				extension.applySchemes(context, operation, method);
				extension.applySecurityRequirements(context, operation, method);
				extension.applyTags(context, operation, method);
				extension.applyResponses(swagger, context, operation, method);
				extension.applyImplicitParameters(swagger, context, operation, method);
				extension.applyExtensions(context, operation, method);
				for (int i = 0; i < genericParameterTypes.length; i++) {
					extension.applyParameters(httpMethod, context, operation, paramAnnotations[i]);
				}

				if ("post".equalsIgnoreCase(httpMethod) && operation.getConsumes() == null) {
					operation.addConsumes("application/x-www-form-urlencoded");
				}
			}

			if (operation.getResponses() == null) {
				operation.defaultResponse(new Response().description("successful operation"));
			}

			final Map<String, String> regexMap = new HashMap<String, String>();
			final String parsedPath = PathUtils.parsePath(operationPath, regexMap);

			Path path = swagger.getPath(parsedPath);
			if (path == null) {
				path = new SwaggerPath();
				swagger.path(parsedPath, path);
			}
			path.set(httpMethod.toLowerCase(), operation);
		}
	}

}
