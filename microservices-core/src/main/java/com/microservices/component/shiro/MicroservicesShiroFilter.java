package com.microservices.component.shiro;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.shiro.web.env.WebEnvironment;
import org.apache.shiro.web.filter.mgt.FilterChainResolver;
import org.apache.shiro.web.servlet.ShiroFilter;
import org.apache.shiro.web.util.WebUtils;

import com.microservices.web.websocket.MicroservicesWebsocketManager;

/**
 * @version V1.0
 * @Package com.microservices.component.shiro
 */
public class MicroservicesShiroFilter extends ShiroFilter {

	private int contextPathLength = 0;

	@Override
	public void init() throws Exception {
		WebEnvironment env = WebUtils.getRequiredWebEnvironment(getServletContext());

		if (env.getServletContext().getContextPath() != null) {
			contextPathLength = env.getServletContext().getContextPath().length();
		}

		setSecurityManager(env.getWebSecurityManager());

		FilterChainResolver resolver = env.getFilterChainResolver();
		if (resolver != null) {
			setFilterChainResolver(resolver);
		}
	}

	@Override
	protected void doFilterInternal(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain) throws ServletException, IOException {
		HttpServletRequest request = (HttpServletRequest) servletRequest;
		HttpServletResponse response = (HttpServletResponse) servletResponse;

		String target = request.getRequestURI();
		if (contextPathLength != 0) {
			target = target.substring(contextPathLength);
		}

		if (target.indexOf('.') != -1 || MicroservicesWebsocketManager.me().isWebsokcetEndPoint(target)) {
			chain.doFilter(request, response);
			return;
		}

		super.doFilterInternal(request, response, chain);
	}
}
