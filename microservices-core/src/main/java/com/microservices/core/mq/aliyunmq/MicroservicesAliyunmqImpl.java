package com.microservices.core.mq.aliyunmq;

import java.util.Properties;

import com.aliyun.openservices.ons.api.Action;
import com.aliyun.openservices.ons.api.ConsumeContext;
import com.aliyun.openservices.ons.api.Consumer;
import com.aliyun.openservices.ons.api.Message;
import com.aliyun.openservices.ons.api.MessageListener;
import com.aliyun.openservices.ons.api.ONSFactory;
import com.aliyun.openservices.ons.api.Producer;
import com.aliyun.openservices.ons.api.PropertyKeyConst;
import com.microservices.Microservices;
import com.microservices.core.mq.Microservicesmq;
import com.microservices.core.mq.MicroservicesmqBase;
import com.microservices.utils.ArrayUtils;

public class MicroservicesAliyunmqImpl extends MicroservicesmqBase implements Microservicesmq, MessageListener {

	private Producer producer;
	private Consumer consumer;

	public MicroservicesAliyunmqImpl() {
		super();

		MicroservicesAliyunmqConfig aliyunmqConfig = Microservices.config(MicroservicesAliyunmqConfig.class);

		Properties properties = new Properties();
		properties.put(PropertyKeyConst.AccessKey, aliyunmqConfig.getAccessKey());// AccessKey 阿里云身份验证，在阿里云服务器管理控制台创建
		properties.put(PropertyKeyConst.SecretKey, aliyunmqConfig.getSecretKey());// SecretKey 阿里云身份验证，在阿里云服务器管理控制台创建
		properties.put(PropertyKeyConst.ProducerId, aliyunmqConfig.getProducerId());// 您在控制台创建的Producer ID
		properties.put(PropertyKeyConst.ONSAddr, aliyunmqConfig.getAddr());
		properties.setProperty(PropertyKeyConst.SendMsgTimeoutMillis, aliyunmqConfig.getSendMsgTimeoutMillis());// 设置发送超时时间，单位毫秒

		producer = ONSFactory.createProducer(properties);
		producer.start();

		if (ArrayUtils.isNotEmpty(this.channels)) {
			initChannelSubscribe(properties);
		}

	}

	private void initChannelSubscribe(Properties properties) {
		consumer = ONSFactory.createConsumer(properties);
		for (String c : channels) {
			consumer.subscribe(c, "*", this);
		}
		consumer.start();
	}

	@Override
	public void enqueue(Object message, String toChannel) {
		throw new RuntimeException("not finished!");
	}

	@Override
	public void publish(Object message, String toChannel) {
		byte[] bytes = Microservices.me().getSerializer().serialize(message);
		Message onsMessage = new Message(toChannel, "*", bytes);
		producer.send(onsMessage);
	}

	@Override
	public Action consume(Message message, ConsumeContext context) {
		byte[] bytes = message.getBody();
		Object object = Microservices.me().getSerializer().deserialize(bytes);
		notifyListeners(message.getTopic(), object);
		return Action.CommitMessage;
	}
}
