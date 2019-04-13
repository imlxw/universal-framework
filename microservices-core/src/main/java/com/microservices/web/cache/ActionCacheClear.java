package com.microservices.web.cache;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.METHOD })
public @interface ActionCacheClear {

	String[] value() default "action_cache_defalut";

}
