package com.microservices.server;

import java.util.LinkedList;
import java.util.List;

import javax.servlet.ServletContextListener;

public class ContextListeners {

	List<Class<? extends ServletContextListener>> listeners = new LinkedList<>();

	public List<Class<? extends ServletContextListener>> getListeners() {
		return listeners;
	}

	public void setListeners(List<Class<? extends ServletContextListener>> listeners) {
		this.listeners = listeners;
	}

	public void addListener(Class<? extends ServletContextListener> clazz) {
		listeners.add(clazz);
	}
}
