package com.microservices.config.client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Timer;
import java.util.TimerTask;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jfinal.kit.LogKit;
import com.microservices.config.PropInfoMap;
import com.microservices.core.http.MicroservicesHttpRequest;
import com.microservices.core.http.MicroservicesHttpResponse;
import com.microservices.core.http.microservices.MicroservicesHttpImpl;
import com.microservices.utils.StringUtils;

/**
 * 定时读取远程配置信息
 *
 * @version V1.0
 * @Package com.microservices.config
 */
public abstract class ConfigRemoteReader {

	private Timer timer;
	private TimerTask task;
	private String url;
	protected String name;
	private int interval;
	private boolean running = false;

	// key : id , value : version
	private final Map<String, String> preScan = new HashMap<>();
	private final Map<String, String> curScan = new HashMap<>();

	private final PropInfoMap remotePropInfoMap = new PropInfoMap();
	private final Properties remoteProperties = new Properties();

	private final MicroservicesHttpImpl http = new MicroservicesHttpImpl();

	public ConfigRemoteReader(String url, String name, int interval) {
		this.url = url;
		this.name = name;
		this.interval = interval;

		initRemoteProps();
	}

	private String httpGet(String url) {
		MicroservicesHttpRequest request = MicroservicesHttpRequest.create(url, null, MicroservicesHttpRequest.METHOD_GET);
		MicroservicesHttpResponse response = http.handle(request);
		return response.isError() ? null : response.getContent();
	}

	/**
	 * 初始化远程配置信息
	 */
	private void initRemoteProps() {
		String jsonString = httpGet(url + "/" + name);

		if (StringUtils.isBlank(jsonString)) {
			LogKit.error("can not get remote config info,plase check url : " + url);
			return;
		}

		JSONObject jsonObject = null;
		try {
			jsonObject = JSON.parseObject(jsonString);
		} catch (Throwable ex) {
			LogKit.error("can not parse json : \n" + jsonString + "\n\nfrom url : " + url, ex);
			return;
		}

		// 先清空本地数据，initRemoteProps 可能被多次调用
		remoteProperties.clear();
		remotePropInfoMap.clear();

		for (String key : jsonObject.keySet()) {
			JSONObject propInfoObject = jsonObject.getJSONObject(key);
			String version = propInfoObject.getString("version");
			JSONObject propertiesObject = propInfoObject.getJSONObject("properties");

			Properties properties = new Properties();
			for (String propertieKey : propertiesObject.keySet()) {
				properties.put(propertieKey, propertiesObject.getString(propertieKey));
				remoteProperties.put(propertieKey, propertiesObject.getString(propertieKey));
			}
			remotePropInfoMap.put(key, new PropInfoMap.PropInfo(version, properties));
		}
	}

	public abstract void onChange(String appName, String key, String oldValue, String newValue);

	private int scanFailTimes = 0;

	private void working() {

		boolean scanSuccess = scan();

		// 扫描失败
		if (!scanSuccess) {

			// 可能是服务挂了
			if (scanFailTimes++ > 5) {
				remoteProperties.clear();
				remotePropInfoMap.clear();
			}
		} else {

			if (scanFailTimes >= 5) {
				initRemoteProps();
			}

			scanFailTimes = 0;
			compare();

			preScan.clear();
			preScan.putAll(curScan);
			curScan.clear();
		}

	}

	/**
	 * 定时扫描远程配置信息
	 */
	private boolean scan() {

		String listUrl = url + "/list";
		String jsonString = httpGet(listUrl);

		if (StringUtils.isBlank(jsonString)) {
			LogKit.error("can not get remote config info,plase check url : " + listUrl);
			return false;
		}

		JSONArray jsonArray = null;
		try {
			jsonArray = JSON.parseArray(jsonString);
		} catch (Throwable ex) {
			LogKit.error("can not parse json : \n" + jsonString + "\n\nfrom url : " + listUrl, ex);
			return false;
		}

		for (int i = 0; i < jsonArray.size(); i++) {
			JSONObject jsonObject = jsonArray.getJSONObject(i);
			curScan.put(jsonObject.getString("id"), jsonObject.getString("version"));
		}

		return true;

	}

	private void compare() {

		// 记录被修改或者新增的文件ID
		List<String> changesIds = new ArrayList<>();

		for (Map.Entry<String, String> entry : curScan.entrySet()) {
			String version = entry.getValue();
			if (preScan.get(entry.getKey()) == null) {
				// 新添加的文件
				changesIds.add(entry.getKey());
			} else if (!version.equals(preScan.get(entry.getKey()))) {
				// 文件被修改了
				changesIds.add(entry.getKey());
			}
		}

		// 记录被删除的文件id
		List<String> deleteIds = new ArrayList<>();
		for (Map.Entry<String, String> entry : preScan.entrySet()) {
			if (curScan.get(entry.getKey()) == null) {
				deleteIds.add(entry.getKey());
			}
		}

		// 有文件 被修改 或者新增了
		for (String changeId : changesIds) {
			String url = this.url + "/" + changeId;
			String jsonString = httpGet(url);

			if (StringUtils.isBlank(jsonString)) {
				LogKit.error("can not get remote config info,plase check url : " + url);
				continue;
			}

			JSONObject jsonObject = null;
			try {
				jsonObject = JSON.parseObject(jsonString);
			} catch (Throwable ex) {
				LogKit.error("can not parse json : \n" + jsonString + "\n\nfrom url : " + url, ex);
				continue;
			}

			for (String key : jsonObject.keySet()) {
				JSONObject propInfoObject = jsonObject.getJSONObject(key);
				String version = propInfoObject.getString("version");
				JSONObject propertiesObject = propInfoObject.getJSONObject("properties");

				Properties properties = new Properties();
				for (String propertieKey : propertiesObject.keySet()) {
					properties.put(propertieKey, propertiesObject.getString(propertieKey));
				}

				PropInfoMap.PropInfo newPropInfo = new PropInfoMap.PropInfo(version, properties);
				PropInfoMap.PropInfo localPropInfo = remotePropInfoMap.get(key);
				remotePropInfoMap.put(key, newPropInfo);

				if (localPropInfo == null)
					continue;

				for (Object newKey : newPropInfo.getProperties().keySet()) {
					String localValue = localPropInfo.getString(newKey);
					String remoteValue = newPropInfo.getString(newKey);
					remoteProperties.put(newKey.toString(), remoteValue);
					if (localValue == null && StringUtils.isNotBlank(remoteValue)) {
						onChange(key, newKey.toString(), null, remoteValue);
					} else if (!localValue.equals(remoteValue)) {
						onChange(key, newKey.toString(), localValue, remoteValue);
					}
				}

				for (Object localKey : localPropInfo.getProperties().keySet()) {
					if (newPropInfo.getString(localKey) == null) {
						remoteProperties.remove(localKey);
						onChange(key, localKey.toString(), localPropInfo.getString(localKey), null);
					}
				}
			}
		}

		/**
		 * 有文件被删除了
		 */
		for (String deleteId : deleteIds) {
			PropInfoMap.PropInfo propInfo = remotePropInfoMap.get(deleteId);
			for (Object key : propInfo.getProperties().keySet()) {
				remoteProperties.remove(key);
				onChange(deleteId, key.toString(), propInfo.getString(key), null);
			}
		}
	}

	public void start() {
		if (!running) {
			timer = new Timer("Microservices-Config-Remote-Reader", true);
			task = new TimerTask() {
				@Override
				public void run() {
					working();
				}
			};
			timer.schedule(task, 0, 1010L * interval);
			running = true;
		}
	}

	public void stop() {
		if (running) {
			timer.cancel();
			task.cancel();
			running = false;
		}
	}

	public Properties getRemoteProperties() {
		return remoteProperties;
	}
}
