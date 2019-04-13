package com.microservices.codegen;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.sql.DataSource;

import com.jfinal.plugin.activerecord.dialect.AnsiSqlDialect;
import com.jfinal.plugin.activerecord.dialect.MysqlDialect;
import com.jfinal.plugin.activerecord.dialect.OracleDialect;
import com.jfinal.plugin.activerecord.dialect.PostgreSqlDialect;
import com.jfinal.plugin.activerecord.dialect.SqlServerDialect;
import com.jfinal.plugin.activerecord.dialect.Sqlite3Dialect;
import com.jfinal.plugin.activerecord.generator.MetaBuilder;
import com.jfinal.plugin.activerecord.generator.TableMeta;
import com.microservices.Microservices;
import com.microservices.db.datasource.DataSourceConfig;
import com.microservices.exception.MicroservicesIllegalConfigException;
import com.microservices.utils.StringUtils;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

/**
 * 代码生成工具类
 */
public class CodeGenHelpler {

	/**
	 * 获取数据源
	 *
	 * @return
	 */
	public static DataSource getDatasource() {
		DataSourceConfig datasourceConfig = Microservices.config(DataSourceConfig.class, "microservices.datasource");
		HikariConfig config = new HikariConfig();
		config.setJdbcUrl(datasourceConfig.getUrl());
		config.setUsername(datasourceConfig.getUser());
		config.setPassword(datasourceConfig.getPassword());
		config.setDriverClassName(datasourceConfig.getDriverClassName());

		return new HikariDataSource(config);
	}

	public static MetaBuilder createMetaBuilder() {
		MetaBuilder metaBuilder = new MetaBuilder(getDatasource());
		DataSourceConfig datasourceConfig = Microservices.config(DataSourceConfig.class, "microservices.datasource");
		switch (datasourceConfig.getType()) {
			case DataSourceConfig.TYPE_MYSQL:
				metaBuilder.setDialect(new MysqlDialect());
				break;
			case DataSourceConfig.TYPE_ORACLE:
				metaBuilder.setDialect(new OracleDialect());
				break;
			case DataSourceConfig.TYPE_SQLSERVER:
				metaBuilder.setDialect(new SqlServerDialect());
				break;
			case DataSourceConfig.TYPE_SQLITE:
				metaBuilder.setDialect(new Sqlite3Dialect());
				break;
			case DataSourceConfig.TYPE_ANSISQL:
				metaBuilder.setDialect(new AnsiSqlDialect());
				break;
			case DataSourceConfig.TYPE_POSTGRESQL:
				metaBuilder.setDialect(new PostgreSqlDialect());
				break;
			default:
				throw new MicroservicesIllegalConfigException("only support datasource type : mysql、orcale、sqlserver、sqlite、ansisql and postgresql, please check your microservices.properties. ");
		}

		return metaBuilder;

	}

	/**
	 * 排除指定的表，有些表不需要生成的
	 *
	 * @param list
	 * @param excludeTables
	 */
	public static void excludeTables(List<TableMeta> list, String excludeTables) {
		if (StringUtils.isNotBlank(excludeTables)) {
			List<TableMeta> newTableMetaList = new ArrayList<>();
			Set<String> excludeTableSet = StringUtils.splitToSet(excludeTables.toLowerCase(), ",");
			for (TableMeta tableMeta : list) {
				if (excludeTableSet.contains(tableMeta.name.toLowerCase())) {
					System.out.println("exclude table : " + tableMeta.name);
					continue;
				}
				newTableMetaList.add(tableMeta);
			}
			list.clear();
			list.addAll(newTableMetaList);
		}
	}

}
