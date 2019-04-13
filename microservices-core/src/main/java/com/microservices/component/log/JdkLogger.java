package com.microservices.component.log;

import java.util.logging.Level;

import com.jfinal.log.Log;
import com.microservices.exception.MicroservicesExceptionHolder;

/**
 * 系统的logger
 */
public class JdkLogger extends Log {

	private java.util.logging.Logger log;
	private String clazzName;

	public JdkLogger(Class<?> clazz) {
		log = java.util.logging.Logger.getLogger(clazz.getName());
		clazzName = clazz.getName();
	}

	public JdkLogger(String name) {
		log = java.util.logging.Logger.getLogger(name);
		clazzName = name;
	}

	@Override
	public void debug(String message) {
		log.logp(Level.FINE, clazzName, Thread.currentThread().getStackTrace()[1].getMethodName(), message);
	}

	@Override
	public void debug(String message, Throwable t) {
		MicroservicesExceptionHolder.hold(t);
		log.logp(Level.FINE, clazzName, Thread.currentThread().getStackTrace()[1].getMethodName(), message, t);
	}

	@Override
	public void info(String message) {
		log.logp(Level.INFO, clazzName, Thread.currentThread().getStackTrace()[1].getMethodName(), message);
	}

	@Override
	public void info(String message, Throwable t) {
		MicroservicesExceptionHolder.hold(t);
		log.logp(Level.INFO, clazzName, Thread.currentThread().getStackTrace()[1].getMethodName(), message, t);
	}

	@Override
	public void warn(String message) {
		log.logp(Level.WARNING, clazzName, Thread.currentThread().getStackTrace()[1].getMethodName(), message);
	}

	@Override
	public void warn(String message, Throwable t) {
		MicroservicesExceptionHolder.hold(t);
		log.logp(Level.WARNING, clazzName, Thread.currentThread().getStackTrace()[1].getMethodName(), message, t);
	}

	@Override
	public void error(String message) {
		log.logp(Level.SEVERE, clazzName, Thread.currentThread().getStackTrace()[1].getMethodName(), message);
	}

	@Override
	public void error(String message, Throwable t) {
		MicroservicesExceptionHolder.hold(t);
		log.logp(Level.SEVERE, clazzName, Thread.currentThread().getStackTrace()[1].getMethodName(), message, t);
	}

	/**
	 * JdkLog fatal is the same as the error.
	 */
	@Override
	public void fatal(String message) {
		log.logp(Level.SEVERE, clazzName, Thread.currentThread().getStackTrace()[1].getMethodName(), message);
	}

	/**
	 * JdkLog fatal is the same as the error.
	 */
	@Override
	public void fatal(String message, Throwable t) {
		MicroservicesExceptionHolder.hold(t);
		log.logp(Level.SEVERE, clazzName, Thread.currentThread().getStackTrace()[1].getMethodName(), message, t);
	}

	@Override
	public boolean isDebugEnabled() {
		return log.isLoggable(Level.FINE);
	}

	@Override
	public boolean isInfoEnabled() {
		return log.isLoggable(Level.INFO);
	}

	@Override
	public boolean isWarnEnabled() {
		return log.isLoggable(Level.WARNING);
	}

	@Override
	public boolean isErrorEnabled() {
		return log.isLoggable(Level.SEVERE);
	}

	@Override
	public boolean isFatalEnabled() {
		return log.isLoggable(Level.SEVERE);
	}
}
