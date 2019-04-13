package com.microservices.db.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Column 的工具类，用于方便组装sql
 */
public class Columns implements Serializable {

	private List<Column> cols = new ArrayList<>();

	public static Columns create() {
		return new Columns();
	}

	public static Columns create(Column column) {
		Columns that = new Columns();
		that.cols.add(column);
		return that;

	}

	public static Columns create(String name, Object value) {
		return create().eq(name, value);
	}

	/**
	 * equals
	 *
	 * @param name
	 * @param value
	 * @return
	 */
	public Columns eq(String name, Object value) {
		cols.add(Column.create(name, value));
		return this;
	}

	/**
	 * not equals !=
	 *
	 * @param name
	 * @param value
	 * @return
	 */
	public Columns ne(String name, Object value) {
		cols.add(Column.create(name, value, Column.LOGIC_NOT_EQUALS));
		return this;
	}

	/**
	 * like
	 *
	 * @param name
	 * @param value
	 * @return
	 */

	public Columns like(String name, Object value) {
		cols.add(Column.create(name, value, Column.LOGIC_LIKE));
		return this;
	}

	/**
	 * 大于 great than
	 *
	 * @param name
	 * @param value
	 * @return
	 */
	public Columns gt(String name, Object value) {
		cols.add(Column.create(name, value, Column.LOGIC_GT));
		return this;
	}

	/**
	 * 大于等于 great or equal
	 *
	 * @param name
	 * @param value
	 * @return
	 */
	public Columns ge(String name, Object value) {
		cols.add(Column.create(name, value, Column.LOGIC_GE));
		return this;
	}

	/**
	 * 小于 less than
	 *
	 * @param name
	 * @param value
	 * @return
	 */
	public Columns lt(String name, Object value) {
		cols.add(Column.create(name, value, Column.LOGIC_LT));
		return this;
	}

	/**
	 * 小于等于 less or equal
	 *
	 * @param name
	 * @param value
	 * @return
	 */
	public Columns le(String name, Object value) {
		cols.add(Column.create(name, value, Column.LOGIC_LE));
		return this;
	}

	public List<Column> getList() {
		return cols;
	}

}
