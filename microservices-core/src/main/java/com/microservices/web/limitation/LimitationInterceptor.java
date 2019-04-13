package com.microservices.web.limitation;

import java.util.concurrent.Semaphore;

import com.google.common.util.concurrent.RateLimiter;
import com.jfinal.core.Controller;
import com.microservices.utils.RequestUtils;
import com.microservices.utils.StringUtils;
import com.microservices.web.fixedinterceptor.FixedInterceptor;
import com.microservices.web.fixedinterceptor.FixedInvocation;

/**
 * 限流拦截器
 */
public class LimitationInterceptor implements FixedInterceptor {

	private static final ThreadLocal<Semaphore> SEMAPHORE_THREAD_LOCAL = new ThreadLocal<>();

	@Override
	public void intercept(FixedInvocation inv) {

		MicroservicesLimitationManager manager = MicroservicesLimitationManager.me();

		LimitationInfo info = manager.getLimitationInfo(inv.getActionKey());
		if (info == null || !info.isEnable()) {
			inv.invoke();
			return;
		}

		if (tryToIntercept(inv, info)) {
			renderLimitation(inv.getController(), info);
			return;
		}

		try {
			inv.invoke();
		} finally {
			if (LimitationInfo.TYPE_CONCURRENCY.equals(info.getType())) {
				SEMAPHORE_THREAD_LOCAL.get().release();
				SEMAPHORE_THREAD_LOCAL.remove();
			}
		}
	}

	private boolean tryToIntercept(FixedInvocation inv, LimitationInfo limitationInfo) {

		switch (limitationInfo.getType()) {
			case LimitationInfo.TYPE_CONCURRENCY:
				return concurrencyIntercept(inv, limitationInfo);
			case LimitationInfo.TYPE_REQUEST:
				return requestIntercept(inv, limitationInfo);
			case LimitationInfo.TYPE_IP:
				return ipIntercept(inv, limitationInfo);
			case LimitationInfo.TYPE_USER:
				return userIntercept(inv, limitationInfo);
		}

		return false;
	}

	private boolean concurrencyIntercept(FixedInvocation inv, LimitationInfo info) {
		MicroservicesLimitationManager manager = MicroservicesLimitationManager.me();
		Semaphore semaphore = manager.getSemaphore(inv.getActionKey());
		if (semaphore == null) {
			semaphore = manager.initSemaphore(inv.getActionKey(), info.getRate());
		}
		boolean acquire = semaphore.tryAcquire();
		if (acquire) {
			SEMAPHORE_THREAD_LOCAL.set(semaphore);
		}
		return !acquire;
	}

	private boolean requestIntercept(FixedInvocation inv, LimitationInfo info) {
		MicroservicesLimitationManager manager = MicroservicesLimitationManager.me();
		RateLimiter limiter = manager.getLimiter(inv.getActionKey());
		if (limiter == null) {
			limiter = manager.initRateLimiter(inv.getActionKey(), info.getRate());
		}
		return !limiter.tryAcquire();
	}

	private boolean ipIntercept(FixedInvocation inv, LimitationInfo info) {
		MicroservicesLimitationManager manager = MicroservicesLimitationManager.me();
		String ipaddress = RequestUtils.getIpAddress(inv.getController().getRequest());
		long currentTime = System.currentTimeMillis();
		long userFlagTime = manager.getIpflag(ipaddress);
		manager.flagIpRequest(ipaddress);

		// 第一次访问，可能manager里还未对此IP进行标识
		if (userFlagTime >= currentTime) {
			return false;
		}

		double rate = info.getRate();
		if (rate <= 0 || rate >= 1000) {
			throw new IllegalArgumentException("@EnablePerIpLimit.rate must > 0 and < 1000");
		}

		double interval = 1000 / rate;
		if ((currentTime - userFlagTime) >= interval) {
			return false;
		}

		return true;
	}

	private boolean userIntercept(FixedInvocation inv, LimitationInfo info) {
		MicroservicesLimitationManager manager = MicroservicesLimitationManager.me();
		String sesssionId = inv.getController().getSession(true).getId();

		long currentTime = System.currentTimeMillis();
		long userFlagTime = manager.getUserflag(sesssionId);

		manager.flagUserRequest(sesssionId);

		// 第一次访问，可能manager里还未对此用户进行标识
		if (userFlagTime >= currentTime) {
			return false;
		}

		double rate = info.getRate();
		if (rate <= 0 || rate >= 1000) {
			throw new IllegalArgumentException("@EnablePerUserLimit.rate must > 0 and < 1000");
		}

		double interval = 1000 / rate;
		if ((currentTime - userFlagTime) >= interval) {
			return false;
		}

		return true;
	}

	private void renderLimitation(Controller controller, LimitationInfo limitationInfo) {

		MicroservicesLimitationManager manager = MicroservicesLimitationManager.me();

		/**
		 * 注解上没有设置 Action , 使用microservices.properties配置文件的
		 */
		if (StringUtils.isBlank(limitationInfo.getRenderType())) {
			// ajax 请求
			if (RequestUtils.isAjaxRequest(controller.getRequest())) {
				controller.renderJson(manager.getAjaxJsonMap());
			}
			// 非ajax的正常请求
			else {
				String limitView = manager.getLimitView();
				if (limitView != null) {
					controller.render(limitView);
				} else {
					controller.renderText("reqeust limit.");
				}
			}
		}

		/**
		 * 设置了 Action , 用用户自己配置的
		 */
		else {
			switch (limitationInfo.getRenderType()) {
				case LimitRenderType.JSON:
					controller.renderJson(limitationInfo.getRenderContent());
					break;
				case LimitRenderType.TEXT:
					controller.renderText(limitationInfo.getRenderContent());
					break;
				case LimitRenderType.RENDER:
					controller.render(limitationInfo.getRenderContent());
					break;
				case LimitRenderType.REDIRECT:
					controller.redirect(limitationInfo.getRenderContent(), true);
					break;
			}

		}
	}

}
