package com.microservices.component.log;

import com.jfinal.log.ILogFactory;
import com.jfinal.log.Log;

public class Slf4jLogFactory implements ILogFactory {

	private static Slf4jLogFactory factory;

	public static Slf4jLogFactory me() {
		if (factory == null) {
			factory = new Slf4jLogFactory();
			factory.slf4jIsOk = new Slf4jLogger("").isOk();
		}
		return factory;
	}

	private boolean slf4jIsOk;

	@Override
	public Log getLog(Class<?> clazz) {
		return slf4jIsOk ? new Slf4jLogger(clazz) : new JdkLogger(clazz);
	}

	@Override
	public Log getLog(String name) {
		return slf4jIsOk ? new Slf4jLogger(name) : new JdkLogger(name);
	}
}
