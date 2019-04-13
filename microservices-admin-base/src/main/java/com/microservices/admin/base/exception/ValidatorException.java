package com.microservices.admin.base.exception;

import com.microservices.exception.MicroservicesException;

/**
 * 校验异常
 */
public class ValidatorException extends MicroservicesException {

	public ValidatorException() {
		super();
	}

	public ValidatorException(String message) {
		super(message);
	}

	public ValidatorException(String message, Throwable cause) {
		super(message, cause);
	}

	public ValidatorException(Throwable cause) {
		super(cause);
	}

	protected ValidatorException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
}
