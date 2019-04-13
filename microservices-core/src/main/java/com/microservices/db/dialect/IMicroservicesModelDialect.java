package com.microservices.db.dialect;

import java.util.List;

import com.microservices.db.model.Column;

public interface IMicroservicesModelDialect {

	public String forFindByColumns(String table, String loadColumns, List<Column> columns, String orderBy, Object limit);

	public String forPaginateSelect(String loadColumns);

	public String forPaginateFrom(String table, List<Column> columns, String orderBy);

}
