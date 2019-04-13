package com.microservices.component.log;

import org.slf4j.LoggerFactory;
import org.slf4j.helpers.NOPLogger;

import com.jfinal.log.Log;
import com.microservices.exception.MicroservicesExceptionHolder;

public class Slf4jLogger extends Log {

	private org.slf4j.Logger logger;

	public Slf4jLogger(Class<?> clazz) {
		logger = LoggerFactory.getLogger(clazz);
	}

	public Slf4jLogger(String name) {
		logger = LoggerFactory.getLogger(name);
	}

	public boolean isOk() {
		return logger.getClass() != NOPLogger.class;
	}

	@Override
	public void debug(String message) {
		logger.debug(message);
	}

	@Override
	public void debug(String message, Throwable t) {
		MicroservicesExceptionHolder.hold(t);
		logger.debug(message, t);
	}

	@Override
	public void info(String message) {
		logger.info(message);
	}

	@Override
	public void info(String message, Throwable t) {
		MicroservicesExceptionHolder.hold(t);
		logger.info(message, t);
	}

	@Override
	public void warn(String message) {
		logger.warn(message);
	}

	@Override
	public void warn(String message, Throwable t) {
		MicroservicesExceptionHolder.hold(t);
		logger.warn(message, t);
	}

	@Override
	public void error(String message) {
		logger.error(message);
	}

	@Override
	public void error(String message, Throwable t) {
		MicroservicesExceptionHolder.hold(t);
		logger.error(message, t);
	}

	@Override
	public void fatal(String message) {
		logger.error(message);
	}

	@Override
	public void fatal(String message, Throwable t) {
		MicroservicesExceptionHolder.hold(t);
		logger.error(message, t);
	}

	@Override
	public boolean isDebugEnabled() {
		return logger.isDebugEnabled();
	}

	@Override
	public boolean isInfoEnabled() {
		return logger.isInfoEnabled();
	}

	@Override
	public boolean isWarnEnabled() {
		return logger.isWarnEnabled();
	}

	@Override
	public boolean isErrorEnabled() {
		return logger.isErrorEnabled();
	}

	@Override
	public boolean isFatalEnabled() {
		return true;
	}
}
