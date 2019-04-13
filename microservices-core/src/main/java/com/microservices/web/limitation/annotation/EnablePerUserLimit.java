package com.microservices.web.limitation.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @version V1.0
 * @Package com.microservices.web.limitation.annotation
 */
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.METHOD })
public @interface EnablePerUserLimit {

	double rate(); // 每秒钟允许通过的次数

	/**
	 * 被限流后给用户的反馈操作 支持：json，render，text，redirect
	 *
	 * @return
	 */
	String renderType() default "";

	/**
	 * 被限流后给客户端的响应，响应的内容根据 action 的类型来渲染
	 *
	 * @return
	 */
	String renderContent() default "";

}
