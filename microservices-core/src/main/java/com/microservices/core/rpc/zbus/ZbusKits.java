package com.microservices.core.rpc.zbus;

/**
 * @version V1.0
 * @Package com.microservices.core.rpc.zbus
 */
public class ZbusKits {

	public static String buildModule(Class clazz, String group, String version) {
		StringBuilder builder = new StringBuilder(clazz.getName());
		builder.append("-").append(group).append("-").append(version);

		return builder.toString();
	}
}
