package com.microservices.db.dialect;

import java.util.List;

import com.jfinal.plugin.activerecord.dialect.MysqlDialect;
import com.microservices.db.model.Column;
import com.microservices.utils.ArrayUtils;
import com.microservices.utils.StringUtils;

public class MicroservicesMysqlDialect extends MysqlDialect implements IMicroservicesModelDialect {

	@Override
	public String forFindByColumns(String table, String loadColumns, List<Column> columns, String orderBy, Object limit) {
		StringBuilder sqlBuilder = new StringBuilder("SELECT ");
		sqlBuilder.append(loadColumns).append(" FROM  `").append(table).append("` ");

		appIfNotEmpty(columns, sqlBuilder);

		if (StringUtils.isNotBlank(orderBy)) {
			sqlBuilder.append(" ORDER BY ").append(orderBy);
		}

		if (limit != null) {
			sqlBuilder.append(" LIMIT " + limit);
		}

		return sqlBuilder.toString();
	}

	@Override
	public String forPaginateSelect(String loadColumns) {
		return "SELECT " + loadColumns;
	}

	@Override
	public String forPaginateFrom(String table, List<Column> columns, String orderBy) {
		StringBuilder sqlBuilder = new StringBuilder(" FROM `").append(table).append("`");

		appIfNotEmpty(columns, sqlBuilder);

		if (StringUtils.isNotBlank(orderBy)) {
			sqlBuilder.append(" ORDER BY ").append(orderBy);
		}

		return sqlBuilder.toString();
	}

	private void appIfNotEmpty(List<Column> columns, StringBuilder sqlBuilder) {
		if (ArrayUtils.isNotEmpty(columns)) {
			sqlBuilder.append(" WHERE ");

			int index = 0;
			for (Column column : columns) {
				sqlBuilder.append(String.format(" `%s` %s ? ", column.getName(), column.getLogic()));
				if (index != columns.size() - 1) {
					sqlBuilder.append(" AND ");
				}
				index++;
			}
		}
	}

}
