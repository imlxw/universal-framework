package com.microservices.web;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class MicroservicesRequestContext {
	private static ThreadLocal<HttpServletRequest> requests = new ThreadLocal<>();
	private static ThreadLocal<HttpServletResponse> responses = new ThreadLocal<>();

	public static void handle(HttpServletRequest req, HttpServletResponse response) {
		requests.set(req);
		responses.set(response);
	}

	public static HttpServletRequest getRequest() {
		return requests.get();
	}

	public static HttpServletResponse getResponse() {
		return responses.get();
	}

	public static void release() {
		requests.remove();
		responses.remove();
	}

	public static <T> T getRequestAttr(String key) {
		HttpServletRequest request = requests.get();
		if (request == null) {
			return null;
		}

		return (T) request.getAttribute(key);
	}

	public static void setRequestAttr(String key, Object value) {
		HttpServletRequest request = requests.get();
		if (request == null) {
			return;
		}

		request.setAttribute(key, value);
	}

}
