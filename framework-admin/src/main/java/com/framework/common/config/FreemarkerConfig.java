package com.framework.common.config;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import com.framework.modules.sys.shiro.ShiroTag;

/**
 * Freemarker配置
 */
@Configuration
public class FreemarkerConfig {

	@Bean
	public FreeMarkerConfigurer freeMarkerConfigurer(ShiroTag shiroTag) {
		FreeMarkerConfigurer configurer = new FreeMarkerConfigurer();
		configurer.setTemplateLoaderPath("classpath:/templates");
		Map<String, Object> variables = new HashMap<>(1);
		variables.put("shiro", shiroTag);
		configurer.setFreemarkerVariables(variables);

		Properties settings = new Properties();
		settings.setProperty("default_encoding", "utf-8");
		settings.setProperty("number_format", "0.##");
		configurer.setFreemarkerSettings(settings);
		return configurer;
	}

}
