package com.microservices.utils;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import com.jfinal.log.Log;
import com.microservices.Microservices;

/**
 * 类实例创建者创建者
 */
public class ClassKits {

	public static Log log = Log.getLog(ClassKits.class);
	private static final Map<Class, Object> singletons = new ConcurrentHashMap<>();

	/**
	 * 获取单例
	 *
	 * @param clazz
	 * @param <T>
	 * @return
	 */
	public static <T> T singleton(Class<T> clazz) {
		Object object = singletons.get(clazz);
		if (object == null) {
			synchronized (clazz) {
				object = singletons.get(clazz);
				if (object == null) {
					object = newInstance(clazz);
					if (object != null) {
						singletons.put(clazz, object);
					} else {
						Log.getLog(clazz).error("cannot new newInstance!!!!");
					}

				}
			}
		}

		return (T) object;
	}

	/**
	 * 创建新的实例
	 *
	 * @param <T>
	 * @param clazz
	 * @return
	 */
	public static <T> T newInstance(Class<T> clazz) {
		return newInstance(clazz, true);
	}

	public static <T> T newInstance(Class<T> clazz, boolean createByGuice) {
		if (createByGuice) {
			return Microservices.bean(clazz);
		} else {
			try {
				Constructor constructor = clazz.getDeclaredConstructor();
				constructor.setAccessible(true);
				return (T) constructor.newInstance();
			} catch (Exception e) {
				log.error("can not newInstance class:" + clazz + "\n" + e.toString(), e);
			}

			return null;
		}
	}

	/**
	 * 创建新的实例
	 *
	 * @param <T>
	 * @param clazzName
	 * @return
	 */
	public static <T> T newInstance(String clazzName) {
		try {
			Class<T> clazz = (Class<T>) Class.forName(clazzName, false, Thread.currentThread().getContextClassLoader());
			return newInstance(clazz);
		} catch (Exception e) {
			log.error("can not newInstance class:" + clazzName + "\n" + e.toString(), e);
		}

		return null;
	}

	public static Class<?> getUsefulClass(Class<?> clazz) {
		// ControllerTest$ServiceTest$$EnhancerByGuice$$40471411#hello
		// com.demo.blog.Blog$$EnhancerByCGLIB$$69a17158
		return clazz.getName().indexOf("$$EnhancerBy") == -1 ? clazz : clazz.getSuperclass();
	}

	/**
	 * 类的set方法缓存，用于减少对类的反射工作
	 */
	private static Multimap<Class<?>, Method> classMethodsCache = Multimaps.synchronizedListMultimap(ArrayListMultimap.create());

	/**
	 * 获取 某class 下的所有set 方法
	 *
	 * @param clazz
	 * @return
	 */
	public static Collection<Method> getClassSetMethods(Class clazz) {

		Collection<Method> setMethods = classMethodsCache.get(clazz);
		if (ArrayUtils.isNullOrEmpty(setMethods)) {
			initSetMethodsCache(clazz);
			setMethods = classMethodsCache.get(clazz);
		}

		return setMethods != null ? new ArrayList<>(setMethods) : null;
	}

	private static void initSetMethodsCache(Class clazz) {
		synchronized (clazz) {
			Method[] methods = clazz.getMethods();
			for (Method method : methods) {
				if (method.getName().startsWith("set") && method.getName().length() > 3 && method.getParameterCount() == 1) {

					classMethodsCache.put(clazz, method);
				}
			}
		}
	}

}
