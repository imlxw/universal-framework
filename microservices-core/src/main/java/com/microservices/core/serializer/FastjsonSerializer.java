package com.microservices.core.serializer;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

public class FastjsonSerializer implements ISerializer {

	@Override
	public byte[] serialize(Object obj) {
		if (obj == null)
			return null;
		FastJsonCacheObject object = new FastJsonCacheObject(obj.getClass(), obj);
		String string = JSON.toJSONString(object);
		return string.getBytes();
	}

	@Override
	public Object deserialize(byte[] bytes) {
		if (bytes == null || bytes.length == 0)
			return null;
		String json = new String(bytes);
		JSONObject jsonObject = JSON.parseObject(json);
		Class clazz = jsonObject.getObject("clazz", Class.class);
		return jsonObject.getObject("object", clazz);
	}

	public static class FastJsonCacheObject {
		private Class clazz;
		private Object object;

		public FastJsonCacheObject() {
		}

		public FastJsonCacheObject(Class clazz, Object object) {
			this.clazz = clazz;
			this.object = object;
		}

		public Class getClazz() {
			return clazz;
		}

		public void setClazz(Class clazz) {
			this.clazz = clazz;
		}

		public Object getObject() {
			return object;
		}

		public void setObject(Object object) {
			this.object = object;
		}
	}

}
