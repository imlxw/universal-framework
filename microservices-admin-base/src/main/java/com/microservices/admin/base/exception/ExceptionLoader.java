package com.microservices.admin.base.exception;

import com.microservices.exception.MicroservicesException;

/**
 * 异常信息获取类
 */
public class ExceptionLoader {

	public static String read(MicroservicesException e) {
		String message = null;

		if (e.getClass() == BusinessException.class) {
			message = e.getMessage();
		} else if (e.getCause() != null && e.getCause().getClass() == BusinessException.class) {
			message = e.getCause().getMessage();
		} else {
			throw e;
		}

		return message;
	}
}
