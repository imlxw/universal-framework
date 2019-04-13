package com.microservices;

import java.io.File;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import com.codahale.metrics.MetricRegistry;
import com.jfinal.kit.PathKit;
import com.microservices.aop.MicroservicesInjectManager;
import com.microservices.component.hystrix.MicroservicesHystrixCommand;
import com.microservices.component.metric.MicroservicesMetricManager;
import com.microservices.component.redis.MicroservicesRedis;
import com.microservices.component.redis.MicroservicesRedisManager;
import com.microservices.config.MicroservicesConfigManager;
import com.microservices.core.cache.MicroservicesCache;
import com.microservices.core.cache.MicroservicesCacheManager;
import com.microservices.core.http.MicroservicesHttp;
import com.microservices.core.http.MicroservicesHttpManager;
import com.microservices.core.http.MicroservicesHttpRequest;
import com.microservices.core.http.MicroservicesHttpResponse;
import com.microservices.core.mq.Microservicesmq;
import com.microservices.core.mq.MicroservicesmqManager;
import com.microservices.core.rpc.Microservicesrpc;
import com.microservices.core.rpc.MicroservicesrpcConfig;
import com.microservices.core.rpc.MicroservicesrpcManager;
import com.microservices.core.serializer.ISerializer;
import com.microservices.core.serializer.SerializerManager;
import com.microservices.event.MicroservicesEvent;
import com.microservices.event.MicroservicesEventManager;
import com.microservices.server.MicroservicesServer;
import com.microservices.server.MicroservicesServerConfig;
import com.microservices.server.MicroservicesServerFactory;
import com.microservices.server.listener.MicroservicesAppListenerManager;
import com.microservices.server.warmboot.AutoDeployManager;
import com.microservices.utils.FileUtils;
import com.microservices.utils.StringUtils;
import com.microservices.web.MicroservicesWebConfig;

/**
 * Microservices 启动类，项目入口
 */
public class Microservices {

	private static Map<String, String> argMap;

	private MicroservicesConfig microservicesConfig;
	private Boolean devMode;
	private Microservicesrpc microservicesrpc;
	private MicroservicesCache microservicesCache;
	private MicroservicesHttp microservicesHttp;
	private MicroservicesRedis microservicesRedis;
	private MicroservicesServer microservicesServer;

	private static Microservices microservices = new Microservices();

	public static Microservices me() {
		return microservices;
	}

	/**
	 * main 入口方法
	 *
	 * @param args
	 */
	public static void main(String[] args) {
		run(args);
	}

	public static void run(String[] args) {
		parseArgs(args);
		microservices.start();
	}

	/**
	 * 解析启动参数
	 *
	 * @param args
	 */
	private static void parseArgs(String[] args) {
		if (args == null || args.length == 0) {
			return;
		}

		for (String arg : args) {
			int indexOf = arg.indexOf("=");
			if (arg.startsWith("--") && indexOf > 0) {
				String key = arg.substring(2, indexOf);
				String value = arg.substring(indexOf + 1);
				setBootArg(key, value);
			}
		}
	}

	public static void setBootArg(String key, Object value) {
		if (argMap == null) {
			argMap = new HashMap<>();
		}
		argMap.put(key, value.toString());
	}

	/**
	 * 获取启动参数
	 *
	 * @param key
	 * @return
	 */
	public static String getBootArg(String key) {
		if (argMap == null)
			return null;
		return argMap.get(key);
	}

	public static Map<String, String> getBootArgs() {
		return argMap;
	}

	/**
	 * 开始启动
	 */
	public void start() {

		printBannerInfo();
		printConfigInfo();

		ensureServerCreated();

		if (!startServer()) {
			System.err.println("microservices start fail!!!");
			return;
		}

		printServerPath();
		printServerUrl();

		if (isDevMode()) {
			AutoDeployManager.me().run();
		}

		MicroservicesAppListenerManager.me().onMicroservicesStarted();

	}

	private boolean startServer() {
		return microservicesServer.start();
	}

	private void ensureServerCreated() {
		if (microservicesServer == null) {
			MicroservicesServerFactory factory = MicroservicesServerFactory.me();
			microservicesServer = factory.buildServer();
		}
	}

	private void printBannerInfo() {
		System.out.println(getBannerText());
	}

	private String getBannerText() {
		MicroservicesConfig config = getMicroservicesConfig();

		if (!config.isBannerEnable()) {
			return "";
		}

		File bannerFile = new File(getRootClassPath(), config.getBannerFile());
		if (bannerFile.exists() && bannerFile.canRead()) {
			String bannerFileText = FileUtils.readString(bannerFile);
			if (StringUtils.isNotBlank(bannerFileText)) {
				return bannerFileText;
			}
		}

		return "  ____  ____    ___    ___   ______ \n" + " |    ||    \\  /   \\  /   \\ |      |\n" + " |__  ||  o  )|     ||     ||      |\n" + " __|  ||     ||  O  ||  O  ||_|  |_|\n" + "/  |  ||  O  ||     ||     |  |  |  \n"
				+ "\\  `  ||     ||     ||     |  |  |  \n" + " \\____||_____| \\___/  \\___/   |__|  \n" + "                                    ";

	}

	private void printConfigInfo() {
		System.out.println(getMicroservicesConfig());
		System.out.println(config(MicroservicesServerConfig.class));
		System.out.println(config(MicroservicesWebConfig.class));
	}

	private void printServerPath() {
		System.out.println("server classPath : " + getRootClassPath());
		System.out.println("server webRoot : " + PathKit.getWebRootPath());
	}

	private void printServerUrl() {
		MicroservicesServerConfig serverConfig = config(MicroservicesServerConfig.class);

		String host = "0.0.0.0".equals(serverConfig.getHost()) ? "127.0.0.1" : serverConfig.getHost();
		String port = "80".equals(serverConfig.getPort()) ? "" : ":" + serverConfig.getPort();
		String path = serverConfig.getContextPath();

		String url = String.format("http://%s%s%s", host, port, path);

		System.out.println("server started success , url : " + url);
	}

	private static String getRootClassPath() {
		String path = null;
		try {
			path = Microservices.class.getClassLoader().getResource("").toURI().getPath();
			return new File(path).getAbsolutePath();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		return path;
	}

	/////////// get component methods///////////

	/**
	 * 是否是开发模式
	 *
	 * @return
	 */
	public boolean isDevMode() {
		if (devMode == null) {
			MicroservicesConfig config = getMicroservicesConfig();
			devMode = MODE.DEV.getValue().equals(config.getMode());
		}
		return devMode;
	}

	/**
	 * 获取MicroservicesConfig 配置文件
	 *
	 * @return
	 */
	public MicroservicesConfig getMicroservicesConfig() {
		if (microservicesConfig == null) {
			microservicesConfig = config(MicroservicesConfig.class);
		}
		return microservicesConfig;
	}

	/**
	 * 获取 Microservicesrpc，进行服务获取和发布
	 *
	 * @return
	 */
	public Microservicesrpc getRpc() {
		if (microservicesrpc == null) {
			microservicesrpc = MicroservicesrpcManager.me().getMicroservicesrpc();
		}
		return microservicesrpc;
	}

	/**
	 * 获取 MQ，进行消息发送
	 *
	 * @return
	 */
	public Microservicesmq getMq() {
		return MicroservicesmqManager.me().getMicroservicesmq();
	}

	/**
	 * 获取 缓存
	 *
	 * @return
	 */
	public MicroservicesCache getCache() {
		if (microservicesCache == null) {
			microservicesCache = MicroservicesCacheManager.me().getCache();
		}
		return microservicesCache;
	}

	/**
	 * 获取 microservicesHttp 工具类，方便操作http请求
	 *
	 * @return
	 */
	public MicroservicesHttp getHttp() {
		if (microservicesHttp == null) {
			microservicesHttp = MicroservicesHttpManager.me().getMicroservicesHttp();
		}
		return microservicesHttp;
	}

	/**
	 * 获取 MicroservicesRedis 工具类，方便操作Redis请求
	 *
	 * @return
	 */
	public MicroservicesRedis getRedis() {
		if (microservicesRedis == null) {
			microservicesRedis = MicroservicesRedisManager.me().getRedis();
		}
		return microservicesRedis;
	}

	/**
	 * 获取本地server 例如，undertow
	 *
	 * @return
	 */
	public MicroservicesServer getServer() {
		return microservicesServer;
	}

	/**
	 * 获取 MetricRegistry
	 *
	 * @return
	 */
	public MetricRegistry getMetric() {
		return MicroservicesMetricManager.me().metric();
	}

	/**
	 * 获取序列化对象
	 *
	 * @return
	 */
	public ISerializer getSerializer() {
		return SerializerManager.me().getSerializer();
	}

	////////// static tool methods///////////

	/**
	 * 获取配置信息
	 *
	 * @param clazz
	 * @param <T>
	 * @return
	 */
	public static <T> T config(Class<T> clazz) {
		return MicroservicesConfigManager.me().get(clazz);
	}

	/**
	 * 读取配置文件信息
	 *
	 * @param clazz
	 * @param prefix
	 * @param <T>
	 * @return
	 */
	public static <T> T config(Class<T> clazz, String prefix) {
		return MicroservicesConfigManager.me().get(clazz, prefix, null);
	}

	/**
	 * 读取配置文件信息
	 *
	 * @param clazz
	 * @param prefix
	 * @param file
	 * @param <T>
	 * @return
	 */
	public static <T> T config(Class<T> clazz, String prefix, String file) {
		return MicroservicesConfigManager.me().get(clazz, prefix, file);
	}

	private MicroservicesrpcConfig rpcConfig;

	/**
	 * 获取 RPC服务
	 *
	 * @param clazz
	 * @param <T>
	 * @return
	 */
	public static <T> T service(Class<T> clazz) {
		if (microservices.rpcConfig == null) {
			microservices.rpcConfig = config(MicroservicesrpcConfig.class);
		}
		return service(clazz, microservices.rpcConfig.getDefaultGroup(), microservices.rpcConfig.getDefaultVersion());
	}

	/**
	 * 获取 RPC 服务
	 *
	 * @param clazz
	 * @param group
	 * @param version
	 * @param <T>
	 * @return
	 */
	public static <T> T service(Class<T> clazz, String group, String version) {
		return microservices.getRpc().serviceObtain(clazz, group, version);
	}

	/**
	 * 向本地系统发送一个事件
	 *
	 * @param event
	 */
	public static void sendEvent(MicroservicesEvent event) {
		MicroservicesEventManager.me().pulish(event);
	}

	/**
	 * 向本地系统发送一个事件
	 *
	 * @param action
	 * @param data
	 */
	public static void sendEvent(String action, Object data) {
		sendEvent(new MicroservicesEvent(action, data));
	}

	/**
	 * http get操作
	 *
	 * @param url
	 * @return
	 */
	public static String httpGet(String url) {
		return httpGet(url, null);
	}

	/**
	 * http get操作
	 *
	 * @param url
	 * @param params
	 * @return
	 */
	public static String httpGet(String url, Map<String, Object> params) {
		MicroservicesHttpRequest request = MicroservicesHttpRequest.create(url, params, MicroservicesHttpRequest.METHOD_GET);
		MicroservicesHttpResponse response = microservices.getHttp().handle(request);
		return response.isError() ? null : response.getContent();
	}

	/**
	 * http post 操作
	 *
	 * @param url
	 * @return
	 */
	public static String httpPost(String url) {
		return httpPost(url, null);
	}

	/**
	 * Http post 操作
	 *
	 * @param url
	 * @param params post的参数，可以是文件
	 * @return
	 */
	public static String httpPost(String url, Map<String, Object> params) {
		MicroservicesHttpRequest request = MicroservicesHttpRequest.create(url, params, MicroservicesHttpRequest.METHOD_POST);
		MicroservicesHttpResponse response = microservices.getHttp().handle(request);
		return response.isError() ? null : response.getContent();
	}

	/**
	 * 获取被增强的，可以使用AOP注入的实体类
	 *
	 * @param clazz
	 * @param <T>
	 * @return
	 */
	public static <T> T bean(Class<T> clazz) {
		return MicroservicesInjectManager.me().getInjector().getInstance(clazz);
	}

	/**
	 * 对某个对象内部的变量进行注入
	 *
	 * @param object
	 */
	public static void injectMembers(Object object) {
		MicroservicesInjectManager.me().getInjector().injectMembers(object);
	}

	/**
	 * 通过 hystrix 进行调用
	 *
	 * @param hystrixRunnable
	 * @param <T>
	 * @return
	 */
	public static <T> T hystrix(MicroservicesHystrixCommand hystrixRunnable) {
		return (T) hystrixRunnable.execute();
	}

	private static Boolean isRunInjar = null;

	/**
	 * 是否在jar包里运行
	 *
	 * @return
	 */
	public static boolean isRunInJar() {
		if (isRunInjar == null) {
			isRunInjar = Thread.currentThread().getContextClassLoader().getResource("") == null;
		}
		return isRunInjar;
	}

	/**
	 * 产品模式：开发、测试、产品
	 */
	public static enum MODE {

		DEV("dev"), TEST("test"), PRODUCT("product");

		private final String value;

		MODE(String value) {
			this.value = value;
		}

		public String getValue() {
			return value;
		}
	}
}
