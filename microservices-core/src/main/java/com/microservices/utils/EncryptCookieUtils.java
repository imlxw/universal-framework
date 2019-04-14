package com.microservices.utils;

import java.math.BigInteger;

import com.jfinal.core.Controller;
import com.jfinal.kit.Base64Kit;
import com.jfinal.kit.HashKit;
import com.jfinal.log.Log;
import com.microservices.Microservices;
import com.microservices.web.MicroservicesWebConfig;

/**
 * 参考：spring-security https://github.com/spring-projects/spring-security/ blob/master/web/src/main/java/org/springframework/security/ web/authentication/rememberme/TokenBasedRememberMeServices.java ....AbstractRememberMeServices.java
 * <p>
 * 加密的cookie工具类
 */
public class EncryptCookieUtils {

	private final static String COOKIE_SEPARATOR = "#";

	private static String COOKIE_ENCRYPT_KEY = Microservices.config(MicroservicesWebConfig.class).getCookieEncryptKey();
	private static Log log = Log.getLog(EncryptCookieUtils.class);

	/**
	 * 在使用之前，小调用此方法进行加密key的设置
	 *
	 * @param key
	 */
	public static void initEncryptKey(String key) {
		COOKIE_ENCRYPT_KEY = key;
	}

	public static void put(Controller ctr, String key, String value) {
		put(ctr, key, value, 60 * 60 * 24 * 7);
	}

	public static void put(Controller ctr, String key, Object value) {
		put(ctr, key, value.toString());
	}

	public static void put(Controller ctr, String key, String value, int maxAgeInSeconds) {
		String cookie = buildCookieValue(value, maxAgeInSeconds);
		ctr.setCookie(key, cookie, maxAgeInSeconds);
	}

	public static void put(Controller ctr, String key, String value, String domain) {
		put(ctr, key, value, 60 * 60 * 24 * 7, domain);
	}

	public static void put(Controller ctr, String key, String value, int maxAgeInSeconds, String domain) {
		String cookie = buildCookieValue(value, maxAgeInSeconds);
		ctr.setCookie(key, cookie, maxAgeInSeconds, null, domain, false);
	}

	public static void remove(Controller ctr, String key) {
		ctr.removeCookie(key);
	}

	public static void remove(Controller ctr, String key, String path, String domain) {
		ctr.removeCookie(key, path, domain);
	}

	public static String get(Controller ctr, String key) {

		String encrypt_key = COOKIE_ENCRYPT_KEY;
		String cookieValue = ctr.getCookie(key);

		if (cookieValue == null) {
			return null;
		}

		String value = new String(Base64Kit.decode(cookieValue));
		return getFromCookieInfo(encrypt_key, value);
	}

	public static String buildCookieValue(String value, int maxAgeInSeconds) {
		String encrypt_key = COOKIE_ENCRYPT_KEY;
		long saveTime = System.currentTimeMillis();
		String encrypt_value = encrypt(encrypt_key, saveTime, maxAgeInSeconds + "", value);

		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append(encrypt_value);
		stringBuilder.append(COOKIE_SEPARATOR);
		stringBuilder.append(saveTime);
		stringBuilder.append(COOKIE_SEPARATOR);
		stringBuilder.append(maxAgeInSeconds);
		stringBuilder.append(COOKIE_SEPARATOR);
		stringBuilder.append(Base64Kit.encode(value));

		return Base64Kit.encode(stringBuilder.toString());
	}

	private static String encrypt(String encrypt_key, long saveTime, String maxAgeInSeconds, String value) {
		if (MicroservicesWebConfig.DEFAULT_COOKIE_ENCRYPT_KEY.equals(encrypt_key)) {
			log.warn("warn!!! encrypt key is defalut value. please config \"microservices.web.cookieEncryptKey = xxx\" in microservices.properties ");
		}
		return HashKit.md5(encrypt_key + saveTime + maxAgeInSeconds + value);
	}

	public static String getFromCookieInfo(String encrypt_key, String cookieValue) {
		if (StringUtils.isNotBlank(cookieValue)) {
			String cookieStrings[] = cookieValue.split(COOKIE_SEPARATOR);
			if (null != cookieStrings && 4 == cookieStrings.length) {
				String encrypt_value = cookieStrings[0];
				String saveTime = cookieStrings[1];
				String maxAgeInSeconds = cookieStrings[2];
				String value = Base64Kit.decodeToStr(cookieStrings[3]);

				String encrypt = encrypt(encrypt_key, Long.valueOf(saveTime), maxAgeInSeconds, value);

				// 保证 cookie 不被人为修改
				if (encrypt_value != null && encrypt_value.equals(encrypt)) {
					long stime = Long.parseLong(saveTime);
					long maxtime = Long.parseLong(maxAgeInSeconds) * 1000;
					// 查看是否过时
					if ((stime + maxtime) - System.currentTimeMillis() > 0) {
						return value;
					}
				}
			}
		}
		return null;
	}

	public static Long getLong(Controller ctr, String key) {
		String value = get(ctr, key);
		return null == value ? null : Long.parseLong(value);
	}

	public static long getLong(Controller ctr, String key, long defalut) {
		String value = get(ctr, key);
		return null == value ? defalut : Long.parseLong(value);
	}

	public static Integer getInt(Controller ctr, String key) {
		String value = get(ctr, key);
		return null == value ? null : Integer.parseInt(value);
	}

	public static int getInt(Controller ctr, String key, int defalut) {
		String value = get(ctr, key);
		return null == value ? defalut : Integer.parseInt(value);
	}

	public static BigInteger getBigInteger(Controller ctr, String key) {
		String value = get(ctr, key);
		return null == value ? null : new BigInteger(value);
	}

	public static BigInteger getBigInteger(Controller ctr, String key, BigInteger defalut) {
		String value = get(ctr, key);
		return null == value ? defalut : new BigInteger(value);
	}

}
