package com.microservices.db.datasource;

import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import com.google.common.collect.Maps;
import com.microservices.Microservices;
import com.microservices.config.MicroservicesConfigManager;
import com.microservices.utils.StringUtils;

public class DataSourceConfigManager {

	private static final String DATASOURCE_PREFIX = "microservices.datasource.";

	private static DataSourceConfigManager manager = new DataSourceConfigManager();

	public static DataSourceConfigManager me() {
		return manager;
	}

	private Map<String, DataSourceConfig> datasourceConfigs = Maps.newHashMap();
	private Map<String, DataSourceConfig> shardingDatasourceConfigs = Maps.newHashMap();

	private DataSourceConfigManager() {

		DataSourceConfig datasourceConfig = Microservices.config(DataSourceConfig.class, "microservices.datasource");

		// 若未配置数据源的名称，设置为默认
		if (StringUtils.isBlank(datasourceConfig.getName())) {
			datasourceConfig.setName(DataSourceConfig.NAME_DEFAULT);
		}

		if (datasourceConfig.isConfigOk()) {
			datasourceConfigs.put(datasourceConfig.getName(), datasourceConfig);
		}

		if (datasourceConfig.isShardingEnable()) {
			shardingDatasourceConfigs.put(datasourceConfig.getName(), datasourceConfig);
		}

		Properties prop = MicroservicesConfigManager.me().getProperties();
		Set<String> datasourceNames = new HashSet<>();
		for (Map.Entry<Object, Object> entry : prop.entrySet()) {
			String key = entry.getKey().toString();
			if (key.startsWith(DATASOURCE_PREFIX) && entry.getValue() != null) {
				String[] keySplits = key.split("\\.");
				if (keySplits.length == 4) {
					datasourceNames.add(keySplits[2]);
				}
			}
		}

		for (String name : datasourceNames) {
			DataSourceConfig dsc = Microservices.config(DataSourceConfig.class, DATASOURCE_PREFIX + name);
			if (StringUtils.isBlank(dsc.getName())) {
				dsc.setName(name);
			}
			if (dsc.isConfigOk()) {
				datasourceConfigs.put(name, dsc);
			}
			if (dsc.isShardingEnable()) {
				shardingDatasourceConfigs.put(name, dsc);
			}
		}
	}

	public Map<String, DataSourceConfig> getDatasourceConfigs() {
		return datasourceConfigs;
	}

	public Map<String, DataSourceConfig> getShardingDatasourceConfigs() {
		return shardingDatasourceConfigs;
	}

}
