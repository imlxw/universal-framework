package com.microservices.core.rpc.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.google.inject.BindingAnnotation;

@Inherited
@BindingAnnotation
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE, ElementType.METHOD, ElementType.CONSTRUCTOR, ElementType.FIELD })
public @interface MicroservicesrpcService {

	Class[] exclude() default Void.class;

	String group() default "";

	String version() default "";

	int port() default 0;
}