package com.framework.common.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.framework.common.exception.RRException;

/**
 * Redis切面处理类
 */
@Aspect
@Component
public class RedisAspect {
	private Logger logger = LoggerFactory.getLogger(getClass());
	// 是否开启redis缓存 true开启 false关闭
	@Value("${framework.redis.open: false}")
	private boolean open;

	@Around("execution(* com.framework.common.utils.RedisUtils.*(..))")
	public Object around(ProceedingJoinPoint point) throws Throwable {
		Object result = null;
		if (open) {
			try {
				result = point.proceed();
			} catch (Exception e) {
				logger.error("redis error", e);
				throw new RRException("Redis服务异常");
			}
		}
		return result;
	}
}
