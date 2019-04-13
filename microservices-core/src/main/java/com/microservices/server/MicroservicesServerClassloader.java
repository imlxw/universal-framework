package com.microservices.server;

public class MicroservicesServerClassloader extends ClassLoader {

	public MicroservicesServerClassloader(ClassLoader parent) {
		super(parent);
	}

	@Override
	public Class<?> loadClass(String name) throws ClassNotFoundException {
		return loadClass(name, false);
	}
}
