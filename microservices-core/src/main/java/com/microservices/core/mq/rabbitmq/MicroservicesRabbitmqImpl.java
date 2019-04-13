package com.microservices.core.mq.rabbitmq;

import java.io.IOException;
import java.util.Map;

import com.google.common.collect.Maps;
import com.jfinal.log.Log;
import com.microservices.Microservices;
import com.microservices.core.mq.Microservicesmq;
import com.microservices.core.mq.MicroservicesmqBase;
import com.microservices.exception.MicroservicesException;
import com.microservices.utils.ArrayUtils;
import com.microservices.utils.StringUtils;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import com.rabbitmq.client.MessageProperties;

/**
 * doc : http://www.rabbitmq.com/api-guide.html
 */
public class MicroservicesRabbitmqImpl extends MicroservicesmqBase implements Microservicesmq {

	private static final Log LOG = Log.getLog(MicroservicesRabbitmqImpl.class);
	private Connection connection;
	private Map<String, Channel> channelMap = Maps.newConcurrentMap();

	public MicroservicesRabbitmqImpl() {
		super();

		MicroservicesmqRabbitmqConfig rabbitmqConfig = Microservices.config(MicroservicesmqRabbitmqConfig.class);

		ConnectionFactory factory = new ConnectionFactory();
		factory.setHost(rabbitmqConfig.getHost());
		factory.setPort(rabbitmqConfig.getPortAsInt());

		if (StringUtils.isNotBlank(rabbitmqConfig.getVirtualHost())) {
			factory.setVirtualHost(rabbitmqConfig.getVirtualHost());
		}
		if (StringUtils.isNotBlank(rabbitmqConfig.getUsername())) {
			factory.setUsername(rabbitmqConfig.getUsername());
		}

		if (StringUtils.isNotBlank(rabbitmqConfig.getPassword())) {
			factory.setPassword(rabbitmqConfig.getPassword());
		}

		try {
			connection = factory.newConnection();
		} catch (Exception e) {
			throw new MicroservicesException("can not connection rabbitmq server", e);
		}

		if (ArrayUtils.isNotEmpty(this.channels)) {
			initChannelSubscribe();
		}

	}

	private void initChannelSubscribe() {
		for (String toChannel : channels) {
			registerListner(getChannel(toChannel), toChannel);
		}
	}

	private void registerListner(final Channel channel, String toChannel) {
		if (channel == null) {
			return;
		}
		try {

			/**
			 * Broadcast listener
			 */
			channel.basicConsume("", true, new DefaultConsumer(channel) {
				@Override
				public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
					Object o = Microservices.me().getSerializer().deserialize(body);
					notifyListeners(envelope.getExchange(), o);
				}
			});

			/**
			 * Queue listener
			 */
			channel.basicConsume(toChannel, true, new DefaultConsumer(channel) {
				@Override
				public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
					Object o = Microservices.me().getSerializer().deserialize(body);
					notifyListeners(envelope.getRoutingKey(), o);
				}
			});

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private Channel getChannel(String toChannel) {

		Channel channel = channelMap.get(toChannel);
		if (channel == null) {
			try {
				channel = connection.createChannel();
				channel.queueDeclare(toChannel, false, false, false, null);
				channel.exchangeDeclare(toChannel, BuiltinExchangeType.FANOUT);
				String queueName = channel.queueDeclare().getQueue();
				channel.queueBind(queueName, toChannel, toChannel);
			} catch (IOException e) {
				throw new MicroservicesException("can not createChannel", e);
			}

			if (channel != null) {
				channelMap.put(toChannel, channel);
			}
		}

		return channel;
	}

	@Override
	public void enqueue(Object message, String toChannel) {
		Channel channel = getChannel(toChannel);
		try {
			byte[] bytes = Microservices.me().getSerializer().serialize(message);
			channel.basicPublish("", toChannel, MessageProperties.BASIC, bytes);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void publish(Object message, String toChannel) {
		Channel channel = getChannel(toChannel);
		try {
			byte[] bytes = Microservices.me().getSerializer().serialize(message);
			channel.basicPublish(toChannel, "", MessageProperties.BASIC, bytes);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
