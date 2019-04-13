package com.microservices.core.mq.zbus;

import java.io.IOException;
import java.util.Map;

import com.google.common.collect.Maps;
import com.jfinal.log.Log;
import com.microservices.Microservices;
import com.microservices.core.mq.Microservicesmq;
import com.microservices.core.mq.MicroservicesmqBase;
import com.microservices.utils.StringUtils;

import io.zbus.mq.Broker;
import io.zbus.mq.ConsumeGroup;
import io.zbus.mq.Consumer;
import io.zbus.mq.ConsumerConfig;
import io.zbus.mq.Message;
import io.zbus.mq.MessageHandler;
import io.zbus.mq.MqClient;
import io.zbus.mq.Producer;

public class MicroservicesZbusmqImpl extends MicroservicesmqBase implements Microservicesmq, MessageHandler {

	private static final Log LOG = Log.getLog(MicroservicesZbusmqImpl.class);
	private Broker broker;

	public MicroservicesZbusmqImpl() {
		super();

		MicroservicesZbusmqConfig zbusmqConfig = Microservices.config(MicroservicesZbusmqConfig.class);
		broker = new Broker(zbusmqConfig.getBroker());

		for (String channel : channels) {
			ConsumerConfig config = new ConsumerConfig(broker);
			config.setTopic(channel);
			config.setMessageHandler(this);
			ConsumeGroup group = ConsumeGroup.createTempBroadcastGroup();
			config.setConsumeGroup(group);
			Consumer consumer = new Consumer(config);

			try {
				consumer.start();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		String queueString = zbusmqConfig.getQueue();
		if (StringUtils.isBlank(queueString)) {
			return;
		}

		String[] queues = queueString.split(",");
		for (String channel : queues) {
			ConsumerConfig config = new ConsumerConfig(broker);
			config.setTopic(channel);
			config.setMessageHandler(this);
			Consumer consumer = new Consumer(config);
			try {
				consumer.start();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void enqueue(Object message, String toChannel) {
		publish(message, toChannel);
	}

	@Override
	public void publish(Object message, String toChannel) {
		Producer producer = getProducer(toChannel);
		Message msg = new Message();
		msg.setTopic(toChannel);
		msg.setBody(Microservices.me().getSerializer().serialize(message));

		try {
			producer.publish(msg);
		} catch (Exception e) {
			LOG.error(e.toString(), e);
		}
	}

	private Map<String, Producer> producerMap = Maps.newConcurrentMap();

	public Producer getProducer(String toChannel) {
		Producer producer = producerMap.get(toChannel);
		if (producer == null) {
			producer = new Producer(broker);
			try {
				producer.declareTopic(toChannel);
			} catch (Exception e) {
				LOG.error(e.toString(), e);
			}
			producerMap.put(toChannel, producer);
		}
		return producer;
	}

	@Override
	public void handle(Message message, MqClient mqClient) throws IOException {
		notifyListeners(message.getTopic(), Microservices.me().getSerializer().deserialize(message.getBody()));
	}
}
