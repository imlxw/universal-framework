package com.microservices.web;

import java.util.HashMap;
import java.util.Map;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.jfinal.core.Controller;
import com.jfinal.core.ControllerFactory;
import com.microservices.Microservices;

/**
 * @version V1.0
 * @Package com.microservices.web
 */
public class MicroservicesControllerManager extends ControllerFactory {

	private static final MicroservicesControllerManager ME = new MicroservicesControllerManager();

	public static MicroservicesControllerManager me() {
		return ME;
	}

	private MicroservicesControllerManager() {
	}

	private ThreadLocal<Map<Class<? extends Controller>, Controller>> buffers = new ThreadLocal<Map<Class<? extends Controller>, Controller>>() {
		@Override
		protected Map<Class<? extends Controller>, Controller> initialValue() {
			return new HashMap<Class<? extends Controller>, Controller>();
		}
	};

	@Override
	public Controller getController(Class<? extends Controller> controllerClass) throws InstantiationException, IllegalAccessException {
		Controller ret = buffers.get().get(controllerClass);
		if (ret == null) {
			ret = controllerClass.newInstance();
			Microservices.injectMembers(ret);
			buffers.get().put(controllerClass, ret);
		}
		return ret;
	}

	private BiMap<String, Class<? extends Controller>> controllerMapping = HashBiMap.create();

	public Class<? extends Controller> getControllerByPath(String path) {
		return controllerMapping.get(path);
	}

	public String getPathByController(Class<? extends Controller> controllerClass) {
		return controllerMapping.inverse().get(controllerClass);
	}

	public void setMapping(String path, Class<? extends Controller> controllerClass) {
		controllerMapping.put(path, controllerClass);
	}

}