package com.microservices.component.shiro;

import java.util.ArrayList;
import java.util.List;

import com.microservices.component.shiro.processer.AuthorizeResult;
import com.microservices.component.shiro.processer.IShiroAuthorizeProcesser;

/**
 * Shiro 认证处理器 执行者
 * <p>
 * 它是对 IShiroAuthorizeProcesser 的几个集合处理
 */
public class ShiroAuthorizeProcesserInvoker {

	List<IShiroAuthorizeProcesser> processers;

	public void addProcesser(IShiroAuthorizeProcesser processer) {
		if (processers == null) {
			processers = new ArrayList<>();
		}
		if (!processers.contains(processer)) {
			processers.add(processer);
		}
	}

	public List<IShiroAuthorizeProcesser> getProcessers() {
		return processers;
	}

	public AuthorizeResult invoke() {
		if (processers == null || processers.size() == 0) {
			return AuthorizeResult.ok();
		}

		for (IShiroAuthorizeProcesser processer : processers) {
			AuthorizeResult result = processer.authorize();

			if (!result.isOk()) {
				return result;
			}
		}

		return AuthorizeResult.ok();
	}

}
