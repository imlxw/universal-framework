package com.microservices.web.cache;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE, ElementType.METHOD })
public @interface EnableActionCache {

	String group() default "action_cache_defalut";

	/**
	 * default 7 days
	 */
	int liveSeconds() default 60 * 60 * 24 * 7;
}
