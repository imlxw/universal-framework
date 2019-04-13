package com.microservices.component.shiro.processer;

import java.io.Serializable;

/**
 * 认证处理器 执行后的认证结果。
 */
public class AuthorizeResult implements Serializable {

	public static final int CODE_SUCCESS = 0;

	/**
	 * 未进行身份认证
	 */
	public static final int ERROR_CODE_UNAUTHENTICATED = 1;

	/**
	 * 没有权限访问
	 */
	public static final int ERROR_CODE_UNAUTHORIZATION = 2;

	private int errorCode = CODE_SUCCESS;

	public int getErrorCode() {
		return errorCode;
	}

	public AuthorizeResult setErrorCode(int errorCode) {
		this.errorCode = errorCode;
		return this;
	}

	public boolean isOk() {
		return errorCode == CODE_SUCCESS;
	}

	public static AuthorizeResult ok() {
		return new AuthorizeResult();
	}

	public static AuthorizeResult fail(int errorCode) {
		return new AuthorizeResult().setErrorCode(errorCode);
	}

}
