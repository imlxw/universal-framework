package com.microservices.web.utils;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

import com.jfinal.core.ActionKey;
import com.jfinal.core.Controller;

/**
 * @version V1.0
 * @Package com.microservices.web.utils
 */
public class ControllerUtils {

	private static final String SLASH = "/";

	/**
	 * 参考ActionMapping中的实现。
	 *
	 * @param controllerClass
	 * @param method
	 * @param controllerKey
	 * @return
	 */
	public static String createActionKey(Class<? extends Controller> controllerClass, Method method, String controllerKey) {
		String methodName = method.getName();
		String actionKey;

		ActionKey ak = method.getAnnotation(ActionKey.class);
		if (ak != null) {
			actionKey = ak.value().trim();
			if ("".equals(actionKey))
				throw new IllegalArgumentException(controllerClass.getName() + "." + methodName + "(): The argument of ActionKey can not be blank.");
			if (!actionKey.startsWith(SLASH))
				actionKey = SLASH + actionKey;
		} else if (methodName.equals("index")) {
			actionKey = controllerKey;
		} else {
			actionKey = controllerKey.equals(SLASH) ? SLASH + methodName : controllerKey + SLASH + methodName;
		}
		return actionKey;
	}

	public static Set<String> buildExcludedMethodName() {
		Set<String> excludedMethodName = new HashSet<String>();
		Method[] methods = Controller.class.getMethods();
		for (Method m : methods) {
			excludedMethodName.add(m.getName());
		}
		return excludedMethodName;
	}

}
