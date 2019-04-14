package com.microservices.config;

import java.io.File;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.jfinal.kit.LogKit;
import com.jfinal.kit.Prop;
import com.jfinal.kit.PropKit;
import com.jfinal.kit.StrKit;
import com.jfinal.log.Log;
import com.microservices.Microservices;
import com.microservices.config.annotation.PropertyConfig;
import com.microservices.config.client.ConfigRemoteReader;
import com.microservices.config.server.ConfigFileScanner;
import com.microservices.exception.MicroservicesIllegalConfigException;
import com.microservices.utils.ArrayUtils;
import com.microservices.utils.ClassKits;
import com.microservices.utils.StringUtils;

/**
 * 配置管理类
 * <p>
 * 用于读取配置信息，包括本地配置信息和分布式远程配置信息
 */
public class MicroservicesConfigManager {

	private static final Log LOG = Log.getLog(MicroservicesConfigManager.class);

	private Properties mainProperties;
	private MicroservicesConfigConfig config;

	private PropInfoMap propInfoMap = new PropInfoMap();

	private ConfigFileScanner configFileScanner;
	private ConfigRemoteReader configRemoteReader;

	private ConcurrentHashMap<String, Object> configs = new ConcurrentHashMap<>();

	/**
	 * 用于在配置文件读取的时候，记录每个key应对的方法，方便在远程配置文件更新的时候 快速的找对对于的方法，然后执行。
	 */
	private Map<String, Method> keyMethodMapping = new ConcurrentHashMap<>();

	/**
	 * 用于记录方法和其已经被实例化的对象 方便方法执行的时候，能找对对应的实例化对象
	 */
	private Multimap<String, Object> keyInstanceMapping = ArrayListMultimap.create();

	private static MicroservicesConfigManager instance;

	public static MicroservicesConfigManager me() {
		if (instance == null) {
			instance = new MicroservicesConfigManager();
		}
		return instance;
	}

	private MicroservicesConfigManager() {
		init();
	}

	private void init() {
		readLocalConfig();
		readRemoteConfig();
	}

	/**
	 * 读取本地配置文件
	 */
	public void readLocalConfig() {
		try {
			Prop prop = PropKit.use("microservices.properties");
			mainProperties = prop.getProperties();
		} catch (Throwable ex) {
			LOG.warn("Could not find microservices.properties in your class path.");
			mainProperties = new Properties();
		}

		initModeProp();

		config = get(MicroservicesConfigConfig.class);

		/**
		 * 定时扫描本地配置文件
		 */
		if (config.isServerEnable()) {
			initConfigFileScanner();
		}
	}

	/**
	 * 读取远程配置文件
	 */
	public void readRemoteConfig() {
		/**
		 * 定时获取远程服务配置信息
		 */
		if (config.isRemoteEnable()) {
			initConfigRemoteReader();
		}
	}

	public <T> T get(Class<T> clazz) {
		PropertyConfig propertyConfig = clazz.getAnnotation(PropertyConfig.class);
		if (propertyConfig == null) {
			return get(clazz, null, null);
		}
		return get(clazz, propertyConfig.prefix(), propertyConfig.file());
	}

	/**
	 * 获取配置信息，并创建和赋值clazz实例
	 *
	 * @param clazz 指定的类
	 * @param prefix 配置文件前缀
	 * @param <T>
	 * @return
	 */
	public <T> T get(Class<T> clazz, String prefix, String file) {

		T obj = (T) configs.get(clazz.getName() + prefix);
		if (obj != null) {
			return obj;
		}

		// 不能通过RPC创建
		// 原因：很多场景下回使用到配置，包括Guice，如果此时又通过Guice来创建Config，会出现循环调用的问题
		obj = ClassKits.newInstance(clazz, false);
		Collection<Method> setMethods = ClassKits.getClassSetMethods(clazz);

		if (ArrayUtils.isNullOrEmpty(setMethods)) {
			configs.put(clazz.getName() + prefix, obj);
			return obj;
		}

		for (Method method : setMethods) {

			String key = getKeyByMethod(prefix, method);
			String value = getValueByKey(key);

			if (StringUtils.isNotBlank(file)) {
				try {
					Prop prop = PropKit.use(file);
					String filePropValue = prop.get(key);
					if (StringUtils.isNotBlank(filePropValue)) {
						value = filePropValue;
					}
				} catch (Throwable ex) {
					LOG.warn("Could not find " + file + " in your class path, use microservices.properties to replace. ");
				}
			}

			/**
			 * 记录 key 和其对于的方法，方便远程配置文件修改的时候，能找到其对于的方法 这里记录了 所有set方法对于的key
			 */
			keyMethodMapping.put(key, method);

			/**
			 * keyInstanceMapping 为 Multimap，一个key有多个object拥有
			 */
			keyInstanceMapping.put(key, obj);

			try {
				if (StringUtils.isNotBlank(value)) {
					Object val = convert(method.getParameterTypes()[0], value);
					method.invoke(obj, val);
				}
			} catch (Throwable ex) {
				LogKit.error(ex.toString(), ex);
			}
		}

		configs.put(clazz.getName() + prefix, obj);

		return obj;
	}

	private String getKeyByMethod(String prefix, Method method) {

		String key = StrKit.firstCharToLowerCase(method.getName().substring(3));

		if (StringUtils.isNotBlank(prefix)) {
			key = prefix.trim() + "." + key;
		}

		return key;
	}

	/**
	 * 根据 key 获取value的值
	 * <p>
	 * 优先获取系统启动设置参数 第二 获取远程配置 第三 获取本地配置
	 *
	 * @param key
	 * @return
	 */
	public String getValueByKey(String key) {

		String value = Microservices.getBootArg(key);

		if (StringUtils.isBlank(value) && configRemoteReader != null) {
			value = (String) configRemoteReader.getRemoteProperties().get(key);
		}

		if (StringUtils.isBlank(value)) {
			value = (String) mainProperties.get(key);
		}
		return value;
	}

	/**
	 * 或者Microservices默认的配置信息
	 *
	 * @return
	 */
	public Properties getProperties() {
		Properties properties = new Properties();

		properties.putAll(mainProperties);

		if (configRemoteReader != null) {
			properties.putAll(configRemoteReader.getRemoteProperties());
		}

		if (Microservices.getBootArgs() != null) {
			for (Map.Entry<String, String> entry : Microservices.getBootArgs().entrySet()) {
				properties.put(entry.getKey(), entry.getValue());
			}
		}

		return properties;
	}

	/**
	 * 初始化不同model下的properties文件
	 */
	private void initModeProp() {
		String mode = (String) mainProperties.get("microservices.mode");
		if (StringUtils.isBlank(mode)) {
			return;
		}

		Prop modeProp = null;
		try {
			String p = String.format("microservices-%s.properties", mode);
			modeProp = PropKit.use(p);
		} catch (Throwable ex) {
		}

		if (modeProp == null) {
			return;
		}

		mainProperties.putAll(modeProp.getProperties());
	}

	/**
	 * 数据转化
	 *
	 * @param type
	 * @param s
	 * @return
	 */
	private static final Object convert(Class<?> type, String s) {

		if (type == String.class) {
			return s;
		}

		if (type == Integer.class || type == int.class) {
			return Integer.parseInt(s);
		} else if (type == Long.class || type == long.class) {
			return Long.parseLong(s);
		} else if (type == Double.class || type == double.class) {
			return Double.parseDouble(s);
		} else if (type == Float.class || type == float.class) {
			return Float.parseFloat(s);
		} else if (type == Boolean.class || type == boolean.class) {
			String value = s.toLowerCase();
			if ("1".equals(value) || "true".equals(value)) {
				return Boolean.TRUE;
			} else if ("0".equals(value) || "false".equals(value)) {
				return Boolean.FALSE;
			} else {
				throw new RuntimeException("Can not parse to boolean type of value: " + s);
			}
		} else if (type == java.math.BigDecimal.class) {
			return new java.math.BigDecimal(s);
		} else if (type == java.math.BigInteger.class) {
			return new java.math.BigInteger(s);
		} else if (type == byte[].class) {
			return s.getBytes();
		}

		throw new MicroservicesIllegalConfigException(type.getName() + " can not be converted, please use other type in your config class!");
	}

	private void initConfigRemoteReader() {
		configRemoteReader = new ConfigRemoteReader(config.getRemoteUrl(), config.getAppName(), 5) {
			@Override
			public void onChange(String appName, String key, String oldValue, String value) {

				if (!appName.equals(name))
					return;
				/**
				 * 过滤掉系统启动参数设置
				 */
				if (Microservices.getBootArg(key) != null) {
					return;
				}

				Method method = keyMethodMapping.get(key);
				if (method == null) {
					LogKit.warn("can not set value to config object when get value from remote， key:" + key + "---value:" + value);
					return;
				}

				Collection<Object> objects = keyInstanceMapping.get(key);
				for (Object obj : objects) {
					try {
						if (StringUtils.isBlank(value)) {
							method.invoke(obj, new Object[] { null });
						} else {
							Object val = convert(method.getParameterTypes()[0], value);
							method.invoke(obj, val);
						}
					} catch (Throwable ex) {
						LogKit.error(ex.toString(), ex);
					}
				}
			}
		};
		configRemoteReader.start();
	}

	private static String getKeyName(String file) {
		File fileio = new File(file);
		file = fileio.getName();
		int index = file.lastIndexOf('.');
		file = file.substring(0, index);
		index = file.lastIndexOf('-');
		if (index < 0) {
			return "microservices";
		} else {
			file = file.substring(index + 1);
			return file;
		}
	}

	private void initConfigFileScanner() {
		configFileScanner = new ConfigFileScanner(config.getPath(), 5) {
			@Override
			public void onChange(String action, String file) {
				switch (action) {
					case ConfigFileScanner.ACTION_ADD:
						propInfoMap.put(getKeyName(file), new PropInfoMap.PropInfo(new File(file)));
						break;
					case ConfigFileScanner.ACTION_DELETE:
						propInfoMap.remove(getKeyName(file));
						break;
					case ConfigFileScanner.ACTION_UPDATE:
						propInfoMap.put(getKeyName(file), new PropInfoMap.PropInfo(new File(file)));
						break;
				}
			}
		};

		configFileScanner.start();
	}

	public PropInfoMap getPropInfoMap() {
		return propInfoMap;
	}

	public void destroy() {
		if (configRemoteReader != null) {
			configRemoteReader.stop();
		}
		if (configFileScanner != null) {
			configFileScanner.stop();
		}
	}

}
