package com.microservices.db.dialect;

import java.util.List;

import com.jfinal.plugin.activerecord.dialect.OracleDialect;
import com.microservices.db.model.Column;
import com.microservices.exception.MicroservicesException;
import com.microservices.utils.ArrayUtils;
import com.microservices.utils.StringUtils;

public class MicroservicesOracleDialect extends OracleDialect implements IMicroservicesModelDialect {

	@Override
	public String forFindByColumns(String table, String loadColumns, List<Column> columns, String orderBy, Object limit) {
		StringBuilder sqlBuilder = new StringBuilder("SELECT ");
		sqlBuilder.append(loadColumns).append(" FROM ").append(table).append(" ");

		appIfNotEmpty(columns, sqlBuilder);

		if (StringUtils.isNotBlank(orderBy)) {
			sqlBuilder.append(" ORDER BY ").append(orderBy);
		}

		if (limit == null) {
			return sqlBuilder.toString();
		}

		if (limit instanceof Number) {
			StringBuilder ret = new StringBuilder();
			ret.append("select * from ( select row_.*, rownum rownum_ from (  ");
			ret.append(sqlBuilder);
			ret.append(" ) row_ where rownum <= ").append(limit).append(") table_alias");
			return ret.toString();
		} else if (limit instanceof String && limit.toString().contains(",")) {
			String[] startAndEnd = limit.toString().split(",");
			String start = startAndEnd[0];
			String end = startAndEnd[1];

			StringBuilder ret = new StringBuilder();
			ret.append("select * from ( select row_.*, rownum rownum_ from (  ");
			ret.append(sqlBuilder);
			ret.append(" ) row_ where rownum <= ").append(end).append(") table_alias");
			ret.append(" where table_alias.rownum_ > ").append(start);
			return ret.toString();
		} else {
			throw new MicroservicesException("sql limit is error!,limit must is Number of String like \"0,10\"");
		}

	}

	@Override
	public String forPaginateSelect(String loadColumns) {
		return "SELECT " + loadColumns;
	}

	@Override
	public String forPaginateFrom(String table, List<Column> columns, String orderBy) {
		StringBuilder sqlBuilder = new StringBuilder(" FROM ").append(table);

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
				sqlBuilder.append(String.format(" %s %s ? ", column.getName(), column.getLogic()));
				if (index != columns.size() - 1) {
					sqlBuilder.append(" AND ");
				}
				index++;
			}
		}
	}

}
