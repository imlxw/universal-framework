package com.microservices.exception;

/**
 * @version V1.0
 * @Title: 配置错误
 * @Description: 在某些情况，必须要指定的某个配置，但是用户没有配置时，抛出该错误
 * @Package com.microservices.exception
 */
public class MicroservicesIllegalConfigException extends MicroservicesException {

	public MicroservicesIllegalConfigException() {
	}

	public MicroservicesIllegalConfigException(String message) {
		super(message);
	}

	public MicroservicesIllegalConfigException(String message, Throwable cause) {
		super(message, cause);
	}

	public MicroservicesIllegalConfigException(Throwable cause) {
		super(cause);
	}

	public MicroservicesIllegalConfigException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
}
