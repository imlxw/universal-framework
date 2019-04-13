package com.microservices.db.model;

import java.io.Serializable;

public class Column implements Serializable {
	public static final String LOGIC_LIKE = " LIKE ";
	public static final String LOGIC_GT = " > ";
	public static final String LOGIC_GE = " >= ";
	public static final String LOGIC_LT = " < ";
	public static final String LOGIC_LE = " <= ";
	public static final String LOGIC_EQUALS = " = ";
	public static final String LOGIC_NOT_EQUALS = " != ";

	private String name;
	private Object value;
	private String logic = LOGIC_EQUALS;

	public static Column create(String name) {
		Column column = new Column();
		column.setName(name);
		return column;
	}

	public static Column create(String name, Object value) {
		Column column = new Column();
		column.setName(name);
		column.setValue(value);
		return column;
	}

	public static Column create(String name, Object value, String logic) {
		Column column = new Column();
		column.setName(name);
		column.setValue(value);
		column.setLogic(logic);
		return column;
	}

	public Column logic(String logic) {
		this.setLogic(logic);
		return this;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		this.value = value;
	}

	public String getLogic() {
		return logic;
	}

	public void setLogic(String logic) {
		this.logic = logic;
	}

	public String toSql() {
		return String.format(" `%s` %s ? ", name, logic);
	}
}
