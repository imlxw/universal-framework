package com.microservices.server;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.Servlet;

public class Servlets {
	Map<String, ServletInfo> servlets = new HashMap<>();

	public Map<String, ServletInfo> getServlets() {
		return servlets;
	}

	public void setServlets(Map<String, ServletInfo> servlets) {
		this.servlets = servlets;
	}

	public void addServlet(String name, ServletInfo info) {
		servlets.put(name, info);
	}

	public static class ServletInfo {
		private Class<? extends Servlet> servletClass;
		private List<String> urlMapping;

		public static ServletInfo create(Class<? extends Servlet> servletClass) {
			ServletInfo info = new ServletInfo();
			info.setServletClass(servletClass);
			return info;
		}

		public Class<? extends Servlet> getServletClass() {
			return servletClass;
		}

		public void setServletClass(Class<? extends Servlet> servletClass) {
			this.servletClass = servletClass;
		}

		public List<String> getUrlMapping() {
			return urlMapping;
		}

		public void setUrlMapping(List<String> urlMapping) {
			this.urlMapping = urlMapping;
		}

		public ServletInfo addUrlMapping(String url) {
			if (urlMapping == null) {
				urlMapping = new ArrayList<>();
			}
			urlMapping.add(url);
			return this;
		}
	}
}
