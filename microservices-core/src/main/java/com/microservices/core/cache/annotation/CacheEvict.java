package com.microservices.core.cache.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.METHOD })
public @interface CacheEvict {
	String name();

	String key() default "";

	String unless() default "";

	boolean beforeInvocation() default false;
}