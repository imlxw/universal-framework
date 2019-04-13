package com.microservices.component.redis;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import com.microservices.Microservices;

/**
 * 参考： com.jfinal.plugin.redis MicroservicesRedis 命令文档: http://redisdoc.com/
 */
public abstract class MicroservicesRedisBase implements MicroservicesRedis {

	@Override
	public byte[] keyToBytes(Object key) {
		return key.toString().getBytes();
	}

	@Override
	public String bytesToKey(byte[] bytes) {
		return new String(bytes);
	}

	@Override
	public byte[][] keysToBytesArray(Object... keys) {
		byte[][] result = new byte[keys.length][];
		for (int i = 0; i < result.length; i++)
			result[i] = keyToBytes(keys[i]);
		return result;
	}

	@Override
	public void fieldSetFromBytesSet(Set<byte[]> data, Set<Object> result) {
		for (byte[] fieldBytes : data) {
			result.add(valueFromBytes(fieldBytes));
		}
	}

	@Override
	public byte[] valueToBytes(Object value) {
		return Microservices.me().getSerializer().serialize(value);
	}

	@Override
	public Object valueFromBytes(byte[] bytes) {
		if (bytes == null || bytes.length == 0) {
			return null;
		}
		return Microservices.me().getSerializer().deserialize(bytes);
	}

	@Override
	public byte[][] valuesToBytesArray(Object... valuesArray) {
		byte[][] data = new byte[valuesArray.length][];
		for (int i = 0; i < data.length; i++)
			data[i] = valueToBytes(valuesArray[i]);
		return data;
	}

	@Override
	public void valueSetFromBytesSet(Set<byte[]> data, Set<Object> result) {
		for (byte[] valueBytes : data) {
			result.add(valueFromBytes(valueBytes));
		}
	}

	@Override
	@SuppressWarnings("rawtypes")
	public List valueListFromBytesList(Collection<byte[]> data) {
		List<Object> result = new ArrayList<Object>();
		for (byte[] d : data) {
			Object object = null;
			try {
				object = valueFromBytes(d);
			} catch (Throwable ex) {
				/**
				 * 有可能出现错误的情况 在类似blpop等命令，会出现把key也返回，key并不是通过序列化转成byte，而是 key.toString().getBytes()
				 */
				object = new String(d);
			}
			result.add(object);
		}
		return result;
	}

}
