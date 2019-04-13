package com.microservices.core.mq;

import java.util.Collection;

public interface Microservicesmq {

	public void enqueue(Object message, String toChannel);

	public void publish(Object message, String toChannel);

	public void addMessageListener(MicroservicesmqMessageListener listener);

	public void addMessageListener(MicroservicesmqMessageListener listener, String forChannel);

	public void removeListener(MicroservicesmqMessageListener listener);

	public void removeAllListeners();

	public Collection<MicroservicesmqMessageListener> getAllChannelListeners();

	public Collection<MicroservicesmqMessageListener> getListenersByChannel(String channel);

}
