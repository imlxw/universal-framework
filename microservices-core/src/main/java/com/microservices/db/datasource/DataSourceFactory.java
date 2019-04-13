package com.microservices.db.datasource;

import javax.sql.DataSource;

public interface DataSourceFactory {

	public DataSource createDataSource(DataSourceConfig dataSourceConfig);

}
