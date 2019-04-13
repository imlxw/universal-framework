package com.microservices.exception;

public class MicroservicesException extends RuntimeException {

	public MicroservicesException() {
		super();
	}

	public MicroservicesException(String message) {
		super(message);
	}

	public MicroservicesException(String message, Throwable cause) {
		super(message, cause);
	}

	public MicroservicesException(Throwable cause) {
		super(cause);
	}

	protected MicroservicesException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
}
