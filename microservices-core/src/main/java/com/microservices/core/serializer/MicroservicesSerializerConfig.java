package com.microservices.core.serializer;

import com.microservices.config.annotation.PropertyConfig;

@PropertyConfig(prefix = "microservices.serializer")
public class MicroservicesSerializerConfig {
	public static final String FST = "fst";
	public static final String FASTJSON = "fastjson";
	public static final String KRYO = "kryo";

	public String type = FST;

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
}
