package com.microservices.db;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.sql.DataSource;

import com.jfinal.kit.PathKit;
import com.jfinal.plugin.activerecord.ActiveRecordPlugin;
import com.jfinal.plugin.activerecord.CaseInsensitiveContainerFactory;
import com.jfinal.plugin.activerecord.Model;
import com.microservices.Microservices;
import com.microservices.core.cache.MicroservicesCache;
import com.microservices.db.datasource.DataSourceBuilder;
import com.microservices.db.datasource.DataSourceConfig;
import com.microservices.db.datasource.DataSourceConfigManager;
import com.microservices.db.dbpro.MicroservicesDbProFactory;
import com.microservices.db.dialect.MicroservicesAnsiSqlDialect;
import com.microservices.db.dialect.MicroservicesMysqlDialect;
import com.microservices.db.dialect.MicroservicesOracleDialect;
import com.microservices.db.dialect.MicroservicesPostgreSqlDialect;
import com.microservices.db.dialect.MicroservicesSqlServerDialect;
import com.microservices.db.dialect.MicroservicesSqlite3Dialect;
import com.microservices.exception.MicroservicesIllegalConfigException;
import com.microservices.utils.ArrayUtils;
import com.microservices.utils.ClassKits;
import com.microservices.utils.StringUtils;

/**
 * 数据库 管理
 */
public class MicroservicesDbManager {
	private static MicroservicesDbManager manager;

	private List<ActiveRecordPlugin> activeRecordPlugins = new ArrayList<>();

	public static MicroservicesDbManager me() {
		if (manager == null) {
			manager = ClassKits.singleton(MicroservicesDbManager.class);
		}
		return manager;
	}

	public MicroservicesDbManager() {

		// 所有的数据源，包含了分库数据源的子数据源
		Map<String, DataSourceConfig> allDatasourceConfigs = DataSourceConfigManager.me().getDatasourceConfigs();

		// 分库的数据源，一个数据源包含了多个数据源。
		Map<String, DataSourceConfig> shardingDatasourceConfigs = DataSourceConfigManager.me().getShardingDatasourceConfigs();

		if (shardingDatasourceConfigs != null && shardingDatasourceConfigs.size() > 0) {
			for (Map.Entry<String, DataSourceConfig> entry : shardingDatasourceConfigs.entrySet()) {

				// 子数据源的配置
				String shardingDatabase = entry.getValue().getShardingDatabase();
				if (StringUtils.isBlank(shardingDatabase)) {
					continue;
				}
				Set<String> databases = StringUtils.splitToSet(shardingDatabase, ",");
				for (String database : databases) {
					DataSourceConfig datasourceConfig = allDatasourceConfigs.remove(database);
					if (datasourceConfig == null) {
						throw new NullPointerException("has no datasource config named " + database + ",plase check your sharding database config");
					}
					entry.getValue().addChildDatasourceConfig(datasourceConfig);
				}
			}
		}

		// 合并后的数据源，包含了分库分表的数据源和正常数据源
		Map<String, DataSourceConfig> mergeDatasourceConfigs = new HashMap<>();
		if (allDatasourceConfigs != null) {
			mergeDatasourceConfigs.putAll(allDatasourceConfigs);
		}

		if (shardingDatasourceConfigs != null) {
			mergeDatasourceConfigs.putAll(shardingDatasourceConfigs);
		}

		for (Map.Entry<String, DataSourceConfig> entry : mergeDatasourceConfigs.entrySet()) {

			DataSourceConfig datasourceConfig = entry.getValue();

			if (datasourceConfig.isConfigOk()) {

				ActiveRecordPlugin activeRecordPlugin = createRecordPlugin(datasourceConfig);
				activeRecordPlugin.setShowSql(Microservices.me().isDevMode());
				activeRecordPlugin.setDbProFactory(new MicroservicesDbProFactory());

				MicroservicesCache microservicesCache = Microservices.me().getCache();
				if (microservicesCache != null) {
					activeRecordPlugin.setCache(microservicesCache);
				}

				configSqlTemplate(datasourceConfig, activeRecordPlugin);
				configDialect(activeRecordPlugin, datasourceConfig);

				activeRecordPlugins.add(activeRecordPlugin);
			}
		}

	}

	/**
	 * 配置 本地 sql
	 *
	 * @param datasourceConfig
	 * @param activeRecordPlugin
	 */
	private void configSqlTemplate(DataSourceConfig datasourceConfig, ActiveRecordPlugin activeRecordPlugin) {
		String sqlTemplatePath = datasourceConfig.getSqlTemplatePath();
		if (StringUtils.isNotBlank(sqlTemplatePath)) {
			if (sqlTemplatePath.startsWith("/")) {
				activeRecordPlugin.setBaseSqlTemplatePath(datasourceConfig.getSqlTemplatePath());
			} else {
				activeRecordPlugin.setBaseSqlTemplatePath(PathKit.getRootClassPath() + "/" + datasourceConfig.getSqlTemplatePath());
			}
		} else {
			activeRecordPlugin.setBaseSqlTemplatePath(PathKit.getRootClassPath());
		}

		String sqlTemplateString = datasourceConfig.getSqlTemplate();
		if (sqlTemplateString != null) {
			String[] sqlTemplateFiles = sqlTemplateString.split(",");
			for (String sql : sqlTemplateFiles) {
				activeRecordPlugin.addSqlTemplate(sql);
			}
		}
	}

	/**
	 * 配置 数据源的 方言
	 *
	 * @param activeRecordPlugin
	 * @param datasourceConfig
	 */
	private void configDialect(ActiveRecordPlugin activeRecordPlugin, DataSourceConfig datasourceConfig) {
		switch (datasourceConfig.getType()) {
			case DataSourceConfig.TYPE_MYSQL:
				activeRecordPlugin.setDialect(new MicroservicesMysqlDialect());
				break;
			case DataSourceConfig.TYPE_ORACLE:
				if (StringUtils.isBlank(datasourceConfig.getContainerFactory())) {
					activeRecordPlugin.setContainerFactory(new CaseInsensitiveContainerFactory());
				}
				activeRecordPlugin.setDialect(new MicroservicesOracleDialect());
				break;
			case DataSourceConfig.TYPE_SQLSERVER:
				activeRecordPlugin.setDialect(new MicroservicesSqlServerDialect());
				break;
			case DataSourceConfig.TYPE_SQLITE:
				activeRecordPlugin.setDialect(new MicroservicesSqlite3Dialect());
				break;
			case DataSourceConfig.TYPE_ANSISQL:
				activeRecordPlugin.setDialect(new MicroservicesAnsiSqlDialect());
				break;
			case DataSourceConfig.TYPE_POSTGRESQL:
				activeRecordPlugin.setDialect(new MicroservicesPostgreSqlDialect());
				break;
			default:
				throw new MicroservicesIllegalConfigException("only support datasource type : mysql、orcale、sqlserver、sqlite、ansisql and postgresql, please check your microservices.properties. ");
		}
	}

	/**
	 * 创建 ActiveRecordPlugin 插件，用于数据库读写
	 *
	 * @param config
	 * @return
	 */
	private ActiveRecordPlugin createRecordPlugin(DataSourceConfig config) {

		String configName = config.getName();
		DataSource dataSource = new DataSourceBuilder(config).build();

		ActiveRecordPlugin activeRecordPlugin = StringUtils.isNotBlank(configName) ? new ActiveRecordPlugin(configName, dataSource) : new ActiveRecordPlugin(dataSource);

		if (StringUtils.isNotBlank(config.getDbProFactory())) {
			activeRecordPlugin.setDbProFactory(ClassKits.newInstance(config.getDbProFactory()));
		}

		if (StringUtils.isNotBlank(config.getContainerFactory())) {
			activeRecordPlugin.setContainerFactory(ClassKits.newInstance(config.getContainerFactory()));
		}

		if (config.getTransactionLevel() != null) {
			activeRecordPlugin.setTransactionLevel(config.getTransactionLevel());
		}

		/**
		 * 不需要添加映射的直接返回
		 */
		if (!config.isNeedAddMapping()) {
			return activeRecordPlugin;
		}

		List<TableInfo> tableInfos = TableInfoManager.me().getTablesInfos(config);
		if (ArrayUtils.isNullOrEmpty(tableInfos)) {
			return activeRecordPlugin;
		}

		for (TableInfo ti : tableInfos) {
			if (StringUtils.isNotBlank(ti.getPrimaryKey())) {
				activeRecordPlugin.addMapping(ti.getTableName(), ti.getPrimaryKey(), (Class<? extends Model<?>>) ti.getModelClass());
			} else {
				activeRecordPlugin.addMapping(ti.getTableName(), (Class<? extends Model<?>>) ti.getModelClass());
			}
		}

		return activeRecordPlugin;
	}

	public List<ActiveRecordPlugin> getActiveRecordPlugins() {
		return activeRecordPlugins;
	}

}
