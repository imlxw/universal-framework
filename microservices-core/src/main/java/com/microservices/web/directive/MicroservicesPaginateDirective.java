package com.microservices.web.directive;

import javax.servlet.http.HttpServletRequest;

import com.microservices.utils.StringUtils;
import com.microservices.web.MicroservicesRequestContext;
import com.microservices.web.directive.annotation.JFinalDirective;
import com.microservices.web.directive.base.PaginateDirectiveBase;

@JFinalDirective("MicroservicesPaginateDirective")
public abstract class MicroservicesPaginateDirective extends PaginateDirectiveBase {

	@Override
	protected String getUrl(int pageNumber) {
		HttpServletRequest request = MicroservicesRequestContext.getRequest();
		String queryString = request.getQueryString();

		String url = request.getRequestURI();

		if (StringUtils.isNotBlank(queryString)) {
			url = url.concat("?").concat(queryString);
		}

		String pageString = "page=";
		int index = url.indexOf(pageString);

		if (index != -1) {
			StringBuilder sb = new StringBuilder();
			sb.append(url.substring(0, index)).append(pageString).append(pageNumber);
			int idx = url.indexOf("&", index);
			if (idx != -1) {
				sb.append(url.substring(idx));
			}
			url = sb.toString();
		} else {
			if (url.contains("?")) {
				url = url.concat(String.format("&page=%s", pageNumber));
			} else {
				url = url.concat(String.format("?page=%s", pageNumber));
			}
		}

		return url;
	}

}