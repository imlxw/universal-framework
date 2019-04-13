package com.microservices.db.model;

import com.microservices.Microservices;
import com.microservices.config.annotation.PropertyConfig;

/**
 * @version V1.0
 * @Package com.microservices.db.model
 */
@PropertyConfig(prefix = "microservices.model")
public class MicroservicesModelConfig {

	private String scan;

	private String columnCreated = "created";
	private String columnModified = "modified";

	public String getScan() {
		return scan;
	}

	public void setScan(String scan) {
		this.scan = scan;
	}

	public String getColumnCreated() {
		return columnCreated;
	}

	public void setColumnCreated(String columnCreated) {
		this.columnCreated = columnCreated;
	}

	public String getColumnModified() {
		return columnModified;
	}

	public void setColumnModified(String columnModified) {
		this.columnModified = columnModified;
	}

	private static MicroservicesModelConfig config;

	public static MicroservicesModelConfig getConfig() {
		if (config == null) {
			config = Microservices.config(MicroservicesModelConfig.class);
		}
		return config;
	}

}
