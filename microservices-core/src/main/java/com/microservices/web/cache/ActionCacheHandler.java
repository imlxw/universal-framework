package com.microservices.web.cache;

import java.io.PrintWriter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.jfinal.core.Action;
import com.jfinal.core.JFinal;
import com.jfinal.handler.Handler;
import com.jfinal.log.Log;
import com.jfinal.render.RenderManager;
import com.microservices.Microservices;
import com.microservices.utils.ArrayUtils;
import com.microservices.utils.StringUtils;
import com.microservices.web.MicroservicesWebConfig;
import com.microservices.web.cache.keygen.ActionKeyGeneratorManager;

public class ActionCacheHandler extends Handler {

	private static String[] urlPara = { null };
	private static Log LOG = Log.getLog(ActionCacheHandler.class);
	private static MicroservicesWebConfig webConfig = Microservices.config(MicroservicesWebConfig.class);

	@Override
	public void handle(String target, HttpServletRequest request, HttpServletResponse response, boolean[] isHandled) {

		if (!webConfig.isActionCacheEnable()) {
			next.handle(target, request, response, isHandled);
			return;
		}

		Action action = JFinal.me().getAction(target, urlPara);
		if (action == null) {
			next.handle(target, request, response, isHandled);
			return;
		}

		ActionCacheClear actionClear = action.getMethod().getAnnotation(ActionCacheClear.class);
		if (actionClear != null) {
			clearActionCache(action, actionClear);
			next.handle(target, request, response, isHandled);
			return;
		}

		EnableActionCache actionCache = getActionCache(action);
		if (actionCache == null) {
			next.handle(target, request, response, isHandled);
			return;
		}

		try {
			exec(target, request, response, isHandled, action, actionCache);
		} finally {
			ActionCacheContext.release();
		}

	}

	/**
	 * 清空 页面缓存
	 *
	 * @param action
	 * @param actionClear
	 */
	private void clearActionCache(Action action, ActionCacheClear actionClear) {
		String[] cacheNames = actionClear.value();
		if (ArrayUtils.isNullOrEmpty(cacheNames)) {
			throw new IllegalArgumentException("ActionCacheClear annotation argument must not be empty " + "in " + action.getControllerClass().getName() + "." + action.getMethodName());
		}

		for (String cacheName : cacheNames) {
			if (StringUtils.isNotBlank(cacheName)) {
				Microservices.me().getCache().removeAll(cacheName);
			}
		}
	}

	public EnableActionCache getActionCache(Action action) {
		EnableActionCache actionCache = action.getMethod().getAnnotation(EnableActionCache.class);
		return actionCache != null ? actionCache : action.getControllerClass().getAnnotation(EnableActionCache.class);
	}

	private void exec(String target, HttpServletRequest request, HttpServletResponse response, boolean[] isHandled, Action action, EnableActionCache actionCacheEnable) {

		// 缓存名称
		String cacheName = actionCacheEnable.group();
		if (StringUtils.isBlank(cacheName)) {
			throw new IllegalArgumentException("EnableActionCache group must not be empty " + "in " + action.getControllerClass().getName() + "." + action.getMethodName());
		}

		if (cacheName.contains("#(") && cacheName.contains(")")) {
			cacheName = regexGetCacheName(cacheName, request);
		}

		// 缓存的key
		String cacheKey = ActionKeyGeneratorManager.me().getGenerator().generate(target, request);
		if (StringUtils.isBlank(cacheKey)) {
			next.handle(target, request, response, isHandled);
			return;
		}

		ActionCacheInfo info = new ActionCacheInfo();
		info.setGroup(cacheName);
		info.setKey(cacheKey);
		info.setLiveSeconds(actionCacheEnable.liveSeconds());

		ActionCacheContext.hold(info);

		ActionCacheContent actionCache = Microservices.me().getCache().get(cacheName, cacheKey);
		if (actionCache == null) {
			next.handle(target, request, response, isHandled);
			return;
		}

		response.setContentType(actionCache.getContentType());
		PrintWriter writer = null;
		try {
			writer = response.getWriter();
			writer.write(actionCache.getContent());
			writer.flush();
			isHandled[0] = true;
		} catch (Exception e) {
			LOG.error(e.toString(), e);
			RenderManager.me().getRenderFactory().getErrorRender(500).setContext(request, response, action.getViewPath()).render();
		} finally {
			if (writer != null) {
				writer.close();
			}
		}
	}

	private static final Pattern pattern = Pattern.compile("#\\(\\S+?\\)");

	private String regexGetCacheName(String cacheName, HttpServletRequest request) {
		Matcher m = pattern.matcher(cacheName);
		while (m.find()) {
			// find 的值 ： #(id)
			String find = m.group(0);
			String parameterName = find.substring(2, find.length() - 1);
			String value = request.getParameter(parameterName);
			if (StringUtils.isBlank(value))
				value = "";
			cacheName = cacheName.replace(find, value);
		}

		return cacheName;
	}

}
