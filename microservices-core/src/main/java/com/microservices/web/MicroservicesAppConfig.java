package com.microservices.web;

import java.sql.Driver;
import java.sql.DriverManager;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import com.jfinal.config.Constants;
import com.jfinal.config.Handlers;
import com.jfinal.config.Interceptors;
import com.jfinal.config.JFinalConfig;
import com.jfinal.config.Plugins;
import com.jfinal.config.Routes;
import com.jfinal.core.Controller;
import com.jfinal.json.FastJsonFactory;
import com.jfinal.json.JsonManager;
import com.jfinal.kit.StrKit;
import com.jfinal.log.Log;
import com.jfinal.plugin.activerecord.ActiveRecordPlugin;
import com.jfinal.template.Engine;
import com.jfinal.template.ext.directive.NowDirective;
import com.jfinal.weixin.sdk.api.ApiConfig;
import com.jfinal.weixin.sdk.api.ApiConfigKit;
import com.microservices.Microservices;
import com.microservices.MicroservicesConstants;
import com.microservices.aop.jfinal.JfinalHandlers;
import com.microservices.aop.jfinal.JfinalPlugins;
import com.microservices.component.log.Slf4jLogFactory;
import com.microservices.component.shiro.MicroservicesShiroManager;
import com.microservices.component.swagger.MicroservicesSwaggerConfig;
import com.microservices.component.swagger.MicroservicesSwaggerController;
import com.microservices.component.swagger.MicroservicesSwaggerManager;
import com.microservices.config.MicroservicesConfigManager;
import com.microservices.core.rpc.MicroservicesrpcManager;
import com.microservices.db.MicroservicesDbManager;
import com.microservices.schedule.MicroservicesScheduleManager;
import com.microservices.server.listener.MicroservicesAppListenerManager;
import com.microservices.utils.ClassKits;
import com.microservices.utils.ClassScanner;
import com.microservices.utils.StringUtils;
import com.microservices.web.cache.ActionCacheHandler;
import com.microservices.web.controller.annotation.RequestMapping;
import com.microservices.web.directive.annotation.JFinalDirective;
import com.microservices.web.directive.annotation.JFinalSharedMethod;
import com.microservices.web.directive.annotation.JFinalSharedObject;
import com.microservices.web.directive.annotation.JFinalSharedStaticMethod;
import com.microservices.web.fixedinterceptor.FixedInterceptors;
import com.microservices.web.handler.MicroservicesActionHandler;
import com.microservices.web.handler.MicroservicesHandler;
import com.microservices.web.limitation.LimitationConfig;
import com.microservices.web.limitation.MicroservicesLimitationManager;
import com.microservices.web.limitation.web.LimitationController;
import com.microservices.web.render.MicroservicesRenderFactory;
import com.microservices.wechat.MicroservicesAccessTokenCache;
import com.microservices.wechat.MicroservicesWechatConfig;

public class MicroservicesAppConfig extends JFinalConfig {

	static final Log log = Log.getLog(MicroservicesAppConfig.class);
	private List<Routes.Route> routeList = new ArrayList<>();

	public MicroservicesAppConfig() {
		Microservices.injectMembers(this);
	}

	@Override
	public void configConstant(Constants constants) {

		constants.setRenderFactory(MicroservicesRenderFactory.me());
		constants.setDevMode(Microservices.me().isDevMode());
		ApiConfigKit.setDevMode(Microservices.me().isDevMode());

		MicroservicesWechatConfig config = Microservices.config(MicroservicesWechatConfig.class);
		ApiConfig apiConfig = config.getApiConfig();
		if (apiConfig != null) {
			ApiConfigKit.putApiConfig(apiConfig);
		}

		constants.setLogFactory(Slf4jLogFactory.me());
		constants.setMaxPostSize(1024 * 1024 * 2000);
		constants.setReportAfterInvocation(false);

		constants.setControllerFactory(MicroservicesControllerManager.me());
		constants.setJsonFactory(new FastJsonFactory());

		MicroservicesAppListenerManager.me().onJfinalConstantConfig(constants);
	}

	@Override
	public void configRoute(Routes routes) {

		List<Class<Controller>> controllerClassList = ClassScanner.scanSubClass(Controller.class);
		if (controllerClassList == null) {
			return;
		}

		for (Class<Controller> clazz : controllerClassList) {
			RequestMapping mapping = clazz.getAnnotation(RequestMapping.class);
			if (mapping == null || mapping.value() == null) {
				continue;
			}

			if (StrKit.notBlank(mapping.viewPath())) {
				routes.add(mapping.value(), clazz, mapping.viewPath());
			} else {
				routes.add(mapping.value(), clazz);
			}
		}

		MicroservicesSwaggerConfig swaggerConfig = Microservices.config(MicroservicesSwaggerConfig.class);
		if (swaggerConfig.isConfigOk()) {
			routes.add(swaggerConfig.getPath(), MicroservicesSwaggerController.class, swaggerConfig.getPath());
		}

		LimitationConfig limitationConfig = Microservices.config(LimitationConfig.class);
		if (StringUtils.isNotBlank(limitationConfig.getWebPath())) {
			routes.add(limitationConfig.getWebPath(), LimitationController.class);
		}

		MicroservicesAppListenerManager.me().onJfinalRouteConfig(routes);

		for (Routes.Route route : routes.getRouteItemList()) {
			MicroservicesControllerManager.me().setMapping(route.getControllerKey(), route.getControllerClass());
		}

		routeList.addAll(routes.getRouteItemList());
	}

	@Override
	public void configEngine(Engine engine) {

		/**
		 * now 并没有被添加到默认的指令当中 查看：EngineConfig
		 */
		engine.addDirective("now", NowDirective.class);

		List<Class> directiveClasses = ClassScanner.scanClass();
		for (Class clazz : directiveClasses) {
			JFinalDirective jFinalDirective = (JFinalDirective) clazz.getAnnotation(JFinalDirective.class);
			if (jFinalDirective != null) {
				engine.addDirective(jFinalDirective.value(), clazz);
			}

			JFinalSharedMethod sharedMethod = (JFinalSharedMethod) clazz.getAnnotation(JFinalSharedMethod.class);
			if (sharedMethod != null) {
				engine.addSharedMethod(ClassKits.newInstance(clazz));
			}

			JFinalSharedStaticMethod sharedStaticMethod = (JFinalSharedStaticMethod) clazz.getAnnotation(JFinalSharedStaticMethod.class);
			if (sharedStaticMethod != null) {
				engine.addSharedStaticMethod(clazz);
			}

			JFinalSharedObject sharedObject = (JFinalSharedObject) clazz.getAnnotation(JFinalSharedObject.class);
			if (sharedObject != null) {
				engine.addSharedObject(sharedObject.value(), ClassKits.newInstance(clazz));
			}
		}

		MicroservicesAppListenerManager.me().onJfinalEngineConfig(engine);
	}

	@Override
	public void configPlugin(Plugins plugins) {

		List<ActiveRecordPlugin> arps = MicroservicesDbManager.me().getActiveRecordPlugins();
		for (ActiveRecordPlugin arp : arps) {
			plugins.add(arp);
		}

		MicroservicesAppListenerManager.me().onJfinalPluginConfig(new JfinalPlugins(plugins));

	}

	@Override
	public void configInterceptor(Interceptors interceptors) {

		MicroservicesAppListenerManager.me().onInterceptorConfig(interceptors);

		MicroservicesAppListenerManager.me().onFixedInterceptorConfig(FixedInterceptors.me());
	}

	@Override
	public void configHandler(Handlers handlers) {

		handlers.add(new ActionCacheHandler());
		handlers.add(new MicroservicesHandler());

		// 用于对jfinal的拦截器进行注入
		handlers.setActionHandler(new MicroservicesActionHandler());

		MicroservicesAppListenerManager.me().onHandlerConfig(new JfinalHandlers(handlers));
	}

	@Override
	public void afterJFinalStart() {
		super.afterJFinalStart();
		ApiConfigKit.setAccessTokenCache(new MicroservicesAccessTokenCache());
		JsonManager.me().setDefaultDatePattern("yyyy-MM-dd HH:mm:ss");

		/**
		 * 初始化
		 */
		MicroservicesrpcManager.me().init();
		MicroservicesShiroManager.me().init(routeList);
		MicroservicesLimitationManager.me().init(routeList);
		MicroservicesScheduleManager.me().init();
		MicroservicesSwaggerManager.me().init();

		/**
		 * 发送启动完成通知
		 */
		Microservices.sendEvent(MicroservicesConstants.EVENT_STARTED, null);

		MicroservicesAppListenerManager.me().onJFinalStarted();
	}

	@Override
	public void beforeJFinalStop() {
		Enumeration<Driver> drivers = DriverManager.getDrivers();
		if (drivers != null) {
			while (drivers.hasMoreElements()) {
				try {
					Driver driver = drivers.nextElement();
					DriverManager.deregisterDriver(driver);
				} catch (Exception e) {
					log.error(e.toString(), e);
				}
			}
		}
		MicroservicesConfigManager.me().destroy();
		MicroservicesAppListenerManager.me().onJFinalStop();
	}

}
