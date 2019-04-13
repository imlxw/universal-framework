package com.microservices.web.controller.validate;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 非空验证注解
 */
@Documented
@Target(ElementType.METHOD)
@Inherited
@Retention(RetentionPolicy.RUNTIME)
public @interface EmptyValidate {

	Form[] value();

	String message() default "";

	String flashMessage() default "";

	String renderType() default ValidateRenderType.DEFAULT;
}
