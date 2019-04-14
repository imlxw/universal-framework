package com.microservices.web.limitation;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import com.google.common.util.concurrent.RateLimiter;
import com.jfinal.config.Routes;
import com.jfinal.core.Controller;
import com.jfinal.kit.Ret;
import com.microservices.Microservices;
import com.microservices.utils.ArrayUtils;
import com.microservices.utils.StringUtils;
import com.microservices.web.limitation.annotation.EnableConcurrencyLimit;
import com.microservices.web.limitation.annotation.EnablePerIpLimit;
import com.microservices.web.limitation.annotation.EnablePerUserLimit;
import com.microservices.web.limitation.annotation.EnableRequestLimit;
import com.microservices.web.utils.ControllerUtils;

/**
 * @version V1.0
 * @Package com.microservices.web.limitation
 */
public class MicroservicesLimitationManager {

	private static final MicroservicesLimitationManager me = new MicroservicesLimitationManager();

	public static MicroservicesLimitationManager me() {
		return me;
	}

	private Map<String, Semaphore> concurrencyRateLimiterMap = new ConcurrentHashMap<>();
	private Map<String, RateLimiter> requestRateLimiterMap = new ConcurrentHashMap<>();
	private Map<String, Object> ajaxJsonMap = new HashMap();
	private String limitView;

	/**
	 * 用户请求记录
	 */
	private LoadingCache<String, Long> userRequestRecord = Caffeine.newBuilder().expireAfterWrite(1, TimeUnit.MINUTES).build(key -> System.currentTimeMillis());

	/**
	 * IP 请求记录
	 */
	private LoadingCache<String, Long> ipRequestRecord = Caffeine.newBuilder().expireAfterWrite(1, TimeUnit.MINUTES).build(key -> System.currentTimeMillis());

	private Map<String, LimitationInfo> concurrencyRates = new ConcurrentHashMap<>();
	private Map<String, LimitationInfo> ipRates = new ConcurrentHashMap<>();
	private Map<String, LimitationInfo> requestRates = new ConcurrentHashMap<>();
	private Map<String, LimitationInfo> userRates = new ConcurrentHashMap<>();

	public void init(List<Routes.Route> routes) {
		initRates(routes);
	}

	/**
	 * 初始化 invokers 变量
	 */
	private void initRates(List<Routes.Route> routes) {
		Set<String> excludedMethodName = ControllerUtils.buildExcludedMethodName();

		for (Routes.Route route : routes) {
			Class<? extends Controller> controllerClass = route.getControllerClass();

			String controllerKey = route.getControllerKey();

			Annotation[] controllerAnnotations = controllerClass.getAnnotations();

			Method[] methods = controllerClass.getMethods();
			for (Method method : methods) {
				if (excludedMethodName.contains(method.getName())) {
					continue;
				}

				Annotation[] methodAnnotations = method.getAnnotations();
				Annotation[] allAnnotations = ArrayUtils.concat(controllerAnnotations, methodAnnotations);

				String actionKey = ControllerUtils.createActionKey(controllerClass, method, controllerKey);

				for (Annotation annotation : allAnnotations) {
					if (annotation.annotationType() == EnableConcurrencyLimit.class) {
						concurrencyRates.put(actionKey, new LimitationInfo((EnableConcurrencyLimit) annotation));
					} else if (annotation.annotationType() == EnablePerIpLimit.class) {
						ipRates.put(actionKey, new LimitationInfo((EnablePerIpLimit) annotation));
					} else if (annotation.annotationType() == EnableRequestLimit.class) {
						requestRates.put(actionKey, new LimitationInfo((EnableRequestLimit) annotation));
					} else if (annotation.annotationType() == EnablePerUserLimit.class) {
						userRates.put(actionKey, new LimitationInfo((EnablePerUserLimit) annotation));
					}
				}
			}
		}
	}

	public LimitationInfo getLimitationInfo(String actionKey) {
		LimitationInfo info = concurrencyRates.get(actionKey);

		if (info != null) {
			return info;
		}

		info = requestRates.get(actionKey);

		if (info != null) {
			return info;
		}

		info = ipRates.get(actionKey);

		if (info != null) {
			return info;
		}

		return userRates.get(actionKey);
	}

	private MicroservicesLimitationManager() {
		LimitationConfig config = Microservices.config(LimitationConfig.class);
		ajaxJsonMap.put("code", config.getLimitAjaxCode());
		ajaxJsonMap.put("message", config.getLimitAjaxMessage());
		this.limitView = config.getLimitView();
	}

	public RateLimiter initRateLimiter(String target, double rate) {
		RateLimiter limiter = requestRateLimiterMap.get(target);
		if (limiter == null) {
			limiter = RateLimiter.create(rate);
			requestRateLimiterMap.put(target, limiter);
			return limiter;
		}

		if (limiter.getRate() == rate) {
			return limiter;
		}

		limiter.setRate(rate);
		requestRateLimiterMap.put(target, limiter);

		return limiter;
	}

	public RateLimiter getLimiter(String target) {
		return requestRateLimiterMap.get(target);
	}

	public Semaphore initSemaphore(String target, double rate) {
		Semaphore semaphore = new Semaphore((int) rate);
		concurrencyRateLimiterMap.put(target, semaphore);
		return semaphore;
	}

	public Semaphore getSemaphore(String target) {
		return concurrencyRateLimiterMap.get(target);
	}

	/**
	 * 标识用户当前请求时间
	 *
	 * @param sessionId
	 */
	public void flagUserRequest(String sessionId) {
		userRequestRecord.put(sessionId, System.currentTimeMillis());
	}

	public long getUserflag(String sessionId) {
		return userRequestRecord.get(sessionId);
	}

	/**
	 * 标识IP地址当前的请求时间
	 *
	 * @param ip
	 */
	public void flagIpRequest(String ip) {
		ipRequestRecord.put(ip, System.currentTimeMillis());
	}

	public long getIpflag(String sessionId) {
		return ipRequestRecord.get(sessionId);
	}

	public Map<String, Object> getAjaxJsonMap() {
		return ajaxJsonMap;
	}

	public String getLimitView() {
		return limitView;
	}

	public Map<String, LimitationInfo> getConcurrencyRates() {
		return concurrencyRates;
	}

	public Map<String, LimitationInfo> getIpRates() {
		return ipRates;
	}

	public Map<String, LimitationInfo> getRequestRates() {
		return requestRates;
	}

	public Map<String, LimitationInfo> getUserRates() {
		return userRates;
	}

	public Ret doProcessEnable(String path, String type, boolean enable) {

		if (StringUtils.isBlank(type)) {
			return Ret.fail().set("message", "type is empty");
		}

		if (StringUtils.isBlank(path)) {
			return Ret.fail().set("message", "path is empty");
		}

		switch (type) {
			case "ip":
				LimitationInfo info = this.getIpRates().get(path);
				if (info == null) {
					return Ret.fail("message", "path not set");
				}
				info.setEnable(enable);
				this.getIpRates().put(path, info);
				break;
			case "user":
				LimitationInfo userInfo = this.getIpRates().get(path);
				if (userInfo == null) {
					return Ret.fail("message", "path not set");
				}
				userInfo.setEnable(enable);
				this.getIpRates().put(path, userInfo);
				break;
			case "request":
				LimitationInfo requestInfo = this.getIpRates().get(path);
				if (requestInfo == null) {
					return Ret.fail("message", "path not set");
				}
				requestInfo.setEnable(enable);
				this.getIpRates().put(path, requestInfo);
				break;
			case "concurrency":
				LimitationInfo concurrencyInfo = this.getIpRates().get(path);
				if (concurrencyInfo == null) {
					return Ret.fail("message", "path not set");
				}
				concurrencyInfo.setEnable(enable);
				this.getIpRates().put(path, concurrencyInfo);
				break;
			default:
				return Ret.fail().set("message", "type is error");
		}

		return Ret.ok();
	}

	public void setIpRates(String path, double rate) {
		LimitationInfo info = this.getIpRates().get(path);
		if (info == null) {
			info = new LimitationInfo();
			info.setType(LimitationInfo.TYPE_IP);
		}
		info.setRate(rate);
		this.getIpRates().put(path, info);
	}

	public void setUserRates(String path, double rate) {
		LimitationInfo info = this.getUserRates().get(path);
		if (info == null) {
			info = new LimitationInfo();
			info.setType(LimitationInfo.TYPE_USER);
		}
		info.setRate(rate);
		this.getUserRates().put(path, info);
	}

	public void setRequestRates(String path, double rate) {
		LimitationInfo info = this.getRequestRates().get(path);
		if (info == null) {
			info = new LimitationInfo();
			info.setType(LimitationInfo.TYPE_REQUEST);
		}
		info.setRate(rate);
		this.getRequestRates().put(path, info);
	}

	public void setConcurrencyRates(String path, double rate) {
		LimitationInfo info = this.getConcurrencyRates().get(path);
		if (info == null) {
			info = new LimitationInfo();
			info.setType(LimitationInfo.TYPE_CONCURRENCY);
		}
		info.setRate(rate);
		this.getConcurrencyRates().put(path, info);
	}
}
