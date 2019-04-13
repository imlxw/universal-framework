package com.microservices.db;

import java.util.Set;

import com.jfinal.plugin.activerecord.Model;

import io.shardingjdbc.core.api.config.strategy.ShardingStrategyConfiguration;

/**
 * @version V1.0
 * @Package com.microservices.db
 */
public class TableInfo {

	private String tableName;
	private String primaryKey;
	private Class<? extends Model> modelClass;
	private Class<? extends ShardingStrategyConfiguration> databaseShardingStrategyConfig;
	private Class<? extends ShardingStrategyConfiguration> tableShardingStrategyConfig;
	private String actualDataNodes;
	private String keyGeneratorColumnName;
	private String keyGeneratorClass;
	private Set<String> datasources;

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public String getPrimaryKey() {
		return primaryKey;
	}

	public void setPrimaryKey(String primaryKey) {
		this.primaryKey = primaryKey;
	}

	public Class<? extends Model> getModelClass() {
		return modelClass;
	}

	public void setModelClass(Class<? extends Model> modelClass) {
		this.modelClass = modelClass;
	}

	public Class<? extends ShardingStrategyConfiguration> getDatabaseShardingStrategyConfig() {
		return databaseShardingStrategyConfig;
	}

	public void setDatabaseShardingStrategyConfig(Class<? extends ShardingStrategyConfiguration> databaseShardingStrategyConfig) {
		this.databaseShardingStrategyConfig = databaseShardingStrategyConfig;
	}

	public Class<? extends ShardingStrategyConfiguration> getTableShardingStrategyConfig() {
		return tableShardingStrategyConfig;
	}

	public void setTableShardingStrategyConfig(Class<? extends ShardingStrategyConfiguration> tableShardingStrategyConfig) {
		this.tableShardingStrategyConfig = tableShardingStrategyConfig;
	}

	public String getActualDataNodes() {
		return actualDataNodes;
	}

	public void setActualDataNodes(String actualDataNodes) {
		this.actualDataNodes = actualDataNodes;
	}

	public String getKeyGeneratorColumnName() {
		return keyGeneratorColumnName;
	}

	public void setKeyGeneratorColumnName(String keyGeneratorColumnName) {
		this.keyGeneratorColumnName = keyGeneratorColumnName;
	}

	public String getKeyGeneratorClass() {
		return keyGeneratorClass;
	}

	public void setKeyGeneratorClass(String keyGeneratorClass) {
		this.keyGeneratorClass = keyGeneratorClass;
	}

	public Set<String> getDatasources() {
		return datasources;
	}

	public void setDatasources(Set<String> datasources) {
		this.datasources = datasources;
	}

}
