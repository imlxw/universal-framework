package com.microservices.db;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.jfinal.plugin.activerecord.Model;
import com.microservices.db.annotation.Table;
import com.microservices.db.datasource.DataSourceConfig;
import com.microservices.db.model.MicroservicesModelConfig;
import com.microservices.utils.ArrayUtils;
import com.microservices.utils.ClassScanner;
import com.microservices.utils.StringUtils;

/**
 * @version V1.0
 * @Package com.microservices.db
 */
public class TableInfoManager {

	private List<TableInfo> allTableInfos;

	private static TableInfoManager instance = new TableInfoManager();

	public static TableInfoManager me() {
		return instance;
	}

	public List<TableInfo> getTablesInfos(DataSourceConfig dataSourceConfig) {
		List<TableInfo> tableInfos = new ArrayList<>();

		Set<String> configTables = null;
		if (StringUtils.isNotBlank(dataSourceConfig.getTable())) {
			configTables = StringUtils.splitToSet(dataSourceConfig.getTable(), ",");
		}

		for (TableInfo tableInfo : getAllTableInfos()) {
			if (tableInfo.getDatasources().contains(dataSourceConfig.getName())) {

				// 如果 datasource.table 已经配置了，就只用这个配置的，不是这个配置的都排除
				if (configTables != null && !configTables.contains(tableInfo.getTableName())) {
					continue;
				}

				tableInfos.add(tableInfo);
			}
		}

		if (StringUtils.isNotBlank(dataSourceConfig.getExTable())) {
			Set<String> configExTables = StringUtils.splitToSet(dataSourceConfig.getExTable(), ",");
			for (Iterator<TableInfo> iterator = tableInfos.iterator(); iterator.hasNext();) {
				TableInfo tableInfo = iterator.next();

				// 如果配置当前数据源的排除表，则需要排除当前数据源的表信息
				if (configExTables.contains(tableInfo.getTableName())) {
					iterator.remove();
				}
			}
		}

		return tableInfos;
	}

	private List<TableInfo> getAllTableInfos() {
		if (allTableInfos == null) {
			allTableInfos = new ArrayList<>();
			initTableInfos(allTableInfos);
		}
		return allTableInfos;
	}

	private void initTableInfos(List<TableInfo> tableInfos) {
		List<Class<Model>> modelClassList = ClassScanner.scanSubClass(Model.class);
		if (ArrayUtils.isNullOrEmpty(modelClassList)) {
			return;
		}

		String scanPackage = MicroservicesModelConfig.getConfig().getScan();

		for (Class<Model> clazz : modelClassList) {
			Table tb = clazz.getAnnotation(Table.class);
			if (tb == null)
				continue;

			if (scanPackage != null && !clazz.getName().startsWith(scanPackage)) {
				continue;
			}

			Set<String> datasources = new HashSet<>();
			if (StringUtils.isNotBlank(tb.datasource())) {
				datasources.addAll(StringUtils.splitToSet(tb.datasource(), ","));
			} else {
				datasources.add(DataSourceConfig.NAME_DEFAULT);
			}

			TableInfo tableInfo = new TableInfo();
			tableInfo.setModelClass(clazz);
			tableInfo.setPrimaryKey(tb.primaryKey());
			tableInfo.setTableName(tb.tableName());
			tableInfo.setDatasources(datasources);

			tableInfo.setActualDataNodes(tb.actualDataNodes());
			tableInfo.setDatabaseShardingStrategyConfig(tb.databaseShardingStrategyConfig());
			tableInfo.setTableShardingStrategyConfig(tb.tableShardingStrategyConfig());

			if (tb.keyGeneratorClass() != null && Void.class != tb.keyGeneratorClass()) {
				tableInfo.setKeyGeneratorClass(tb.keyGeneratorClass().getName());
			}
			tableInfo.setKeyGeneratorColumnName(tb.keyGeneratorColumnName());

			tableInfos.add(tableInfo);
		}

	}
}
