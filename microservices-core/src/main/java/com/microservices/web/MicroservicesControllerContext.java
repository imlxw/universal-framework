package com.microservices.web;

import com.jfinal.core.Controller;

/**
 * @version V1.0
 * @Package com.microservices.web
 */
public class MicroservicesControllerContext {

	private static ThreadLocal<Controller> controllers = new ThreadLocal<>();

	public static void hold(Controller controller) {
		controllers.set(controller);
	}

	public static Controller get() {
		return controllers.get();
	}

	public static void release() {
		controllers.remove();
	}

}