package com.microservices.component.redis.lettuce;

import java.nio.ByteBuffer;

import io.lettuce.core.codec.RedisCodec;

/**
 * @version V1.0
 */
public class MicroservicesLettuceCodec implements RedisCodec<Object, Object> {

	@Override
	public Object decodeKey(ByteBuffer bytes) {
		return null;
	}

	@Override
	public Object decodeValue(ByteBuffer bytes) {
		return null;
	}

	@Override
	public ByteBuffer encodeKey(Object key) {
		return null;
	}

	@Override
	public ByteBuffer encodeValue(Object value) {
		return null;
	}
}
