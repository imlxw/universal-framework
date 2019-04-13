package com.microservices.admin.base.exception;

import com.microservices.exception.MicroservicesException;

/**
 * 系统异常
 */
public class SystemException extends MicroservicesException {

	public SystemException() {
		super();
	}

	public SystemException(String message) {
		super(message);
	}

	public SystemException(String message, Throwable cause) {
		super(message, cause);
	}

	public SystemException(Throwable cause) {
		super(cause);
	}

	protected SystemException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
}
