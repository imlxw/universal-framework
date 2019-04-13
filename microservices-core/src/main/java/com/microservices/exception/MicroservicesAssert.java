package com.microservices.exception;

/**
 * 断言
 */
public class MicroservicesAssert {

	public static void assertNull(Object object, String message) {
		if (object != null) {
			throw new MicroservicesException(message);
		}
	}

	public static void assertNotNull(Object object, String message) {
		if (object == null) {
			throw new MicroservicesException(message);
		}

	}

	public static void assertFalse(boolean condition, String message) {
		if (condition) {
			throw new MicroservicesException(message);
		}
	}

	public static void assertTrue(boolean condition, String message) {
		if (!condition) {
			throw new MicroservicesException(message);
		}
	}
}
