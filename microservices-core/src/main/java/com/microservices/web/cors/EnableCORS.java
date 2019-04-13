package com.microservices.web.cors;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @version V1.0
 * @Package com.microservices.web.cors
 *          <p>
 *          detail : https://developer.mozilla.org/en-US/docs/Glossary/CORS
 */
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.METHOD, ElementType.TYPE })
public @interface EnableCORS {

	String allowOrigin() default "";

	String allowCredentials() default "";

	String allowHeaders() default "";

	String allowMethods() default "";

	String exposeHeaders() default "";

	String requestHeaders() default "";

	String requestMethod() default "";

	String origin() default "";

	int maxAge() default 0;
}
