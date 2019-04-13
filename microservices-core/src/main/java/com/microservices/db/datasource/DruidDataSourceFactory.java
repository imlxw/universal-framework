package com.microservices.db.datasource;

import java.sql.SQLException;

import javax.sql.DataSource;

import com.alibaba.druid.pool.DruidDataSource;
import com.google.common.collect.Sets;
import com.jfinal.log.Log;

/**
 * @version V1.0
 * @Package com.microservices.db.datasource
 */
public class DruidDataSourceFactory implements DataSourceFactory {

	static Log log = Log.getLog(DruidDataSourceFactory.class);

	@Override
	public DataSource createDataSource(DataSourceConfig dataSourceConfig) {

		DruidDataSource druidDataSource = new DruidDataSource();
		druidDataSource.setUrl(dataSourceConfig.getUrl());
		druidDataSource.setUsername(dataSourceConfig.getUser());
		druidDataSource.setPassword(dataSourceConfig.getPassword());
		druidDataSource.setDriverClassName(dataSourceConfig.getDriverClassName());
		try {
			druidDataSource.setFilters("stat");
		} catch (SQLException e) {
			log.error("DruidDataSourceFactory is error", e);
		}

		if (dataSourceConfig.getConnectionInitSql() != null) {
			druidDataSource.setConnectionInitSqls(Sets.newHashSet(dataSourceConfig.getConnectionInitSql()));
		}

		return druidDataSource;
	}
}
