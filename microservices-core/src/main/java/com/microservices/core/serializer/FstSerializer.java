package com.microservices.core.serializer;

import org.nustaq.serialization.FSTConfiguration;

public class FstSerializer implements ISerializer {

	static FSTConfiguration fst = FSTConfiguration.createDefaultConfiguration();

	@Override
	public byte[] serialize(Object obj) {
		if (obj == null)
			return null;
		return fst.asByteArray(obj);
	}

	@Override
	public Object deserialize(byte[] bytes) {
		if (bytes == null || bytes.length == 0)
			return null;
		return fst.asObject(bytes);
	}

}
