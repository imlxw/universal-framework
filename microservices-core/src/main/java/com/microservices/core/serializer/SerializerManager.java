package com.microservices.core.serializer;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.microservices.Microservices;
import com.microservices.core.spi.MicroservicesSpiLoader;
import com.microservices.exception.MicroservicesAssert;
import com.microservices.utils.ClassKits;

public class SerializerManager {

	private static SerializerManager me;

	private static Map<String, ISerializer> serializerMap = new ConcurrentHashMap<>();

	public static SerializerManager me() {
		if (me == null) {
			me = ClassKits.singleton(SerializerManager.class);
		}
		return me;
	}

	public ISerializer getSerializer() {
		MicroservicesSerializerConfig config = Microservices.config(MicroservicesSerializerConfig.class);
		return getSerializer(config.getType());
	}

	public ISerializer getSerializer(String serializerString) {

		ISerializer serializer = serializerMap.get(serializerString);

		if (serializer == null) {

			serializer = buildSerializer(serializerString);
			serializerMap.put(serializerString, serializer);
		}

		return serializer;
	}

	private ISerializer buildSerializer(String serializerString) {

		MicroservicesAssert.assertTrue(serializerString != null, "can not get serializer config, please set microservices.serializer value to microservices.proerties");

		/**
		 * 可能是某个类名
		 */
		if (serializerString != null && serializerString.contains(".")) {

			ISerializer serializer = ClassKits.newInstance(serializerString);

			if (serializer != null) {
				return serializer;
			}
		}

		switch (serializerString) {
			case MicroservicesSerializerConfig.KRYO:
				return new KryoSerializer();
			case MicroservicesSerializerConfig.FST:
				return new FstSerializer();
			case MicroservicesSerializerConfig.FASTJSON:
				return new FastjsonSerializer();

			default:
				return MicroservicesSpiLoader.load(ISerializer.class, serializerString);
		}
	}

}
