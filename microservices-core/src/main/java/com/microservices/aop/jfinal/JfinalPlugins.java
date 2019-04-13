package com.microservices.aop.jfinal;

import com.jfinal.config.Plugins;
import com.jfinal.plugin.IPlugin;
import com.microservices.Microservices;

/**
 * Jfinal Plugins 的代理类，方便为Plugin插件的自动注入功能
 */
public class JfinalPlugins {

	private final Plugins plugins;

	public JfinalPlugins(Plugins plugins) {
		this.plugins = plugins;
	}

	public JfinalPlugins add(IPlugin plugin) {
		Microservices.injectMembers(plugin);
		plugins.add(plugin);
		return this;
	}
}
