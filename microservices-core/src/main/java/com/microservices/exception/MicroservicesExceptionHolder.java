package com.microservices.exception;

import java.util.ArrayList;
import java.util.List;

public class MicroservicesExceptionHolder {

	static ThreadLocal<List<Throwable>> throwables = new ThreadLocal<>();

	public static void init() {
		throwables.set(new ArrayList<>());
	}

	public static void release() {
		throwables.get().clear();
		throwables.remove();
	}

	public static void hold(Throwable ex) {
		List<Throwable> list = throwables.get();
		if (list != null) {
			list.add(ex);
		}
	}

	public static List<Throwable> throwables() {
		return throwables.get();
	}

}
