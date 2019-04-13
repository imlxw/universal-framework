package com.microservices.component.shiro;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.apache.shiro.authz.annotation.RequiresGuest;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.apache.shiro.authz.annotation.RequiresUser;

import com.jfinal.config.Routes;
import com.jfinal.core.Controller;
import com.microservices.Microservices;
import com.microservices.component.shiro.processer.AuthorizeResult;
import com.microservices.component.shiro.processer.ShiroClear;
import com.microservices.component.shiro.processer.ShiroRequiresAuthenticationProcesser;
import com.microservices.component.shiro.processer.ShiroRequiresGuestProcesser;
import com.microservices.component.shiro.processer.ShiroRequiresPermissionsProcesser;
import com.microservices.component.shiro.processer.ShiroRequiresRolesProcesser;
import com.microservices.component.shiro.processer.ShiroRequiresUserProcesser;
import com.microservices.exception.MicroservicesIllegalConfigException;
import com.microservices.utils.ArrayUtils;
import com.microservices.utils.ClassKits;
import com.microservices.utils.StringUtils;
import com.microservices.web.utils.ControllerUtils;

/**
 * shiro 管理器.
 */
public class MicroservicesShiroManager {
	private static MicroservicesShiroManager me = new MicroservicesShiroManager();

	private MicroservicesShiroConfig microservicesShiroConfig = Microservices.config(MicroservicesShiroConfig.class);

	private MicroservicesShiroManager() {
	}

	public static MicroservicesShiroManager me() {
		return me;
	}

	private ConcurrentHashMap<String, ShiroAuthorizeProcesserInvoker> invokers = new ConcurrentHashMap<>();

	private ShiroRequiresAuthenticationProcesser requiresAuthenticationProcesser = new ShiroRequiresAuthenticationProcesser();
	private ShiroRequiresUserProcesser requiresUserProcesser = new ShiroRequiresUserProcesser();
	private ShiroRequiresGuestProcesser requiresGuestProcesser = new ShiroRequiresGuestProcesser();

	public void init(List<Routes.Route> routes) {
		initInvokers(routes);
	}

	/**
	 * 初始化 invokers 变量
	 */
	private void initInvokers(List<Routes.Route> routes) {
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

				if (method.getAnnotation(ShiroClear.class) != null) {
					continue;
				}

				Annotation[] methodAnnotations = method.getAnnotations();
				Annotation[] allAnnotations = ArrayUtils.concat(controllerAnnotations, methodAnnotations);

				String actionKey = ControllerUtils.createActionKey(controllerClass, method, controllerKey);
				ShiroAuthorizeProcesserInvoker invoker = new ShiroAuthorizeProcesserInvoker();

				for (Annotation annotation : allAnnotations) {
					if (annotation.annotationType() == RequiresPermissions.class) {
						ShiroRequiresPermissionsProcesser processer = new ShiroRequiresPermissionsProcesser((RequiresPermissions) annotation);
						invoker.addProcesser(processer);
					} else if (annotation.annotationType() == RequiresRoles.class) {
						ShiroRequiresRolesProcesser processer = new ShiroRequiresRolesProcesser((RequiresRoles) annotation);
						invoker.addProcesser(processer);
					} else if (annotation.annotationType() == RequiresUser.class) {
						invoker.addProcesser(requiresUserProcesser);
					} else if (annotation.annotationType() == RequiresAuthentication.class) {
						invoker.addProcesser(requiresAuthenticationProcesser);
					} else if (annotation.annotationType() == RequiresGuest.class) {
						invoker.addProcesser(requiresGuestProcesser);
					}
				}

				if (invoker.getProcessers() != null && invoker.getProcessers().size() > 0) {
					invokers.put(actionKey, invoker);
				}

			}
		}
	}

	public AuthorizeResult invoke(String actionKey) {
		ShiroAuthorizeProcesserInvoker invoker = invokers.get(actionKey);
		if (invoker == null) {
			return AuthorizeResult.ok();
		}

		return invoker.invoke();
	}

	private MicroservicesShiroInvokeListener invokeListener;

	public MicroservicesShiroInvokeListener getInvokeListener() {

		if (invokeListener != null) {
			return invokeListener;
		}

		invokeListener = MicroservicesShiroInvokeListener.DEFAULT;

		if (StringUtils.isNotBlank(microservicesShiroConfig.getInvokeListener())) {
			invokeListener = ClassKits.newInstance(microservicesShiroConfig.getInvokeListener());
			if (invokeListener == null) {
				throw new MicroservicesIllegalConfigException("can not find Class : " + microservicesShiroConfig.getInvokeListener() + " please config microservices.shiro.invokeListener correct. ");
			}
		}

		return invokeListener;
	}

}
