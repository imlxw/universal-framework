package com.microservices.core.mq.activemq;

import com.microservices.core.mq.Microservicesmq;
import com.microservices.core.mq.MicroservicesmqBase;

public class MicroservicesActivemq extends MicroservicesmqBase implements Microservicesmq {

	@Override
	public void enqueue(Object message, String toChannel) {

	}

	@Override
	public void publish(Object message, String toChannel) {

	}
}
