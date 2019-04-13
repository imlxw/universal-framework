package com.microservices.component.shiro.processer;

/**
 * Shiro 的认证处理器 用于对每个controller 的 每个方法进行认证
 */
public interface IShiroAuthorizeProcesser {

	public AuthorizeResult authorize();

}
