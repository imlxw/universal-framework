package com.microservices.db.datasource;

import javax.sql.DataSource;

import com.microservices.Microservices;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

/**
 * @version V1.0
 * @Package com.microservices.db.datasource
 */
public class HikariDataSourceFactory implements DataSourceFactory {

	@Override
	public DataSource createDataSource(DataSourceConfig dataSourceConfig) {

		HikariConfig hikariConfig = new HikariConfig();
		hikariConfig.setJdbcUrl(dataSourceConfig.getUrl());
		hikariConfig.setUsername(dataSourceConfig.getUser());
		hikariConfig.setPassword(dataSourceConfig.getPassword());
		hikariConfig.addDataSourceProperty("cachePrepStmts", dataSourceConfig.isCachePrepStmts());
		hikariConfig.addDataSourceProperty("prepStmtCacheSize", dataSourceConfig.getPrepStmtCacheSize());
		hikariConfig.addDataSourceProperty("prepStmtCacheSqlLimit", dataSourceConfig.getPrepStmtCacheSqlLimit());

		hikariConfig.setDriverClassName(dataSourceConfig.getDriverClassName());
		hikariConfig.setPoolName(dataSourceConfig.getPoolName());

		if (dataSourceConfig.getConnectionInitSql() != null) {
			hikariConfig.setConnectionInitSql(dataSourceConfig.getConnectionInitSql());
		}

		hikariConfig.setMaximumPoolSize(dataSourceConfig.getMaximumPoolSize());

		HikariDataSource dataSource = new HikariDataSource(hikariConfig);
		dataSource.setMetricRegistry(Microservices.me().getMetric());

		return dataSource;
	}
}
