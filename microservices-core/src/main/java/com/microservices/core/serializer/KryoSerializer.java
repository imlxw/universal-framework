package com.microservices.core.serializer;

import java.io.ByteArrayOutputStream;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.ByteBufferInput;
import com.esotericsoftware.kryo.io.Output;
import com.esotericsoftware.kryo.pool.KryoFactory;
import com.esotericsoftware.kryo.pool.KryoPool;

/**
 * @version V1.0
 * @Title: Kryo 序列化
 * @Description: 性能和 fst一样
 * @Package com.microservices.core.serializer
 */
public class KryoSerializer implements ISerializer {

	private KryoFactory kryoFactory = new KryoFactory() {
		@Override
		public Kryo create() {
			return new Kryo();
		}
	};

	private KryoPool kryoPool = new KryoPool.Builder(kryoFactory).softReferences().build();

	@Override
	public byte[] serialize(Object obj) {
		if (obj == null)
			return null;
		Output output = null;
		Kryo kryo = kryoPool.borrow();
		try {
			output = new Output(new ByteArrayOutputStream());
			kryo.writeClassAndObject(output, obj);
			return output.toBytes();
		} finally {
			if (output != null) {
				output.close();
			}
			kryoPool.release(kryo);
		}
	}

	@Override
	public Object deserialize(byte[] bytes) {
		if (bytes == null || bytes.length == 0)
			return null;
		ByteBufferInput input = null;
		Kryo kryo = kryoPool.borrow();
		try {
			input = new ByteBufferInput(bytes);
			return kryo.readClassAndObject(input);
		} finally {
			if (input != null) {
				input.close();
			}
			kryoPool.release(kryo);
		}
	}
}
