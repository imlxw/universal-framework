package com.microservices.component.redis.lettuce;

/**
 * @version V1.0
 */
public class LettuceException extends RuntimeException {

	public LettuceException() {
		super();
	}

	public LettuceException(String message) {
		super(message);
	}

	public LettuceException(String message, Throwable cause) {
		super(message, cause);
	}

	public LettuceException(Throwable cause) {
		super(cause);
	}

	protected LettuceException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
}
