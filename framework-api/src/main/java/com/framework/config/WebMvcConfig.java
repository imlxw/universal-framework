package com.framework.config;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.framework.interceptor.AuthorizationInterceptor;
import com.framework.resolver.LoginUserHandlerMethodArgumentResolver;

/**
 * MVC配置
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
	@Autowired
	private AuthorizationInterceptor authorizationInterceptor;
	@Autowired
	private LoginUserHandlerMethodArgumentResolver loginUserHandlerMethodArgumentResolver;

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(authorizationInterceptor).addPathPatterns("/api/**");
	}

	@Override
	public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
		argumentResolvers.add(loginUserHandlerMethodArgumentResolver);
	}
}