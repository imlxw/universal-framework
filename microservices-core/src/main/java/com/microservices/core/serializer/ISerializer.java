package com.microservices.core.serializer;

public interface ISerializer {

	public byte[] serialize(Object obj);

	public Object deserialize(byte[] bytes);
}
