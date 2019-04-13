package com.microservices.core.mq;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import com.jfinal.log.Log;
import com.microservices.Microservices;
import com.microservices.utils.StringUtils;

public abstract class MicroservicesmqBase implements Microservicesmq {

	private static final Log LOG = Log.getLog(MicroservicesmqBase.class);

	private List<MicroservicesmqMessageListener> allChannelListeners = new CopyOnWriteArrayList<>();
	private Multimap<String, MicroservicesmqMessageListener> listenersMap = ArrayListMultimap.create();
	protected MicroservicesmqConfig config = Microservices.config(MicroservicesmqConfig.class);

	protected Set<String> channels = Sets.newHashSet();
	protected Set<String> syncRecevieMessageChannels = Sets.newHashSet();

	private final ExecutorService threadPool = new ThreadPoolExecutor(0, Integer.MAX_VALUE, 60L, TimeUnit.SECONDS, new SynchronousQueue<Runnable>());

	public MicroservicesmqBase() {
		String channelString = config.getChannel();
		if (StringUtils.isBlank(channelString)) {
			return;
		}

		this.channels.addAll(StringUtils.splitToSet(channelString, ","));

		if (StringUtils.isNotBlank(config.getSyncRecevieMessageChannel())) {
			this.syncRecevieMessageChannels.addAll(StringUtils.splitToSet(config.getSyncRecevieMessageChannel(), ","));
		}
	}

	@Override
	public void addMessageListener(MicroservicesmqMessageListener listener) {
		allChannelListeners.add(listener);
	}

	@Override
	public void addMessageListener(MicroservicesmqMessageListener listener, String forChannel) {
		String[] forChannels = forChannel.split(",");
		for (String channel : forChannels) {
			if (StringUtils.isBlank(channel)) {
				continue;
			}
			listenersMap.put(channel.trim(), listener);
		}
	}

	@Override
	public void removeListener(MicroservicesmqMessageListener listener) {
		allChannelListeners.remove(listener);
		for (String channel : listenersMap.keySet()) {
			listenersMap.remove(channel, listener);
		}
	}

	@Override
	public void removeAllListeners() {
		allChannelListeners.clear();
		listenersMap.clear();
	}

	@Override
	public Collection<MicroservicesmqMessageListener> getAllChannelListeners() {
		return allChannelListeners;
	}

	@Override
	public Collection<MicroservicesmqMessageListener> getListenersByChannel(String channel) {
		return listenersMap.get(channel);
	}

	public void notifyListeners(String channel, Object message) {
		boolean globalResult = notifyAll(channel, message, allChannelListeners);
		boolean channelResult = notifyAll(channel, message, listenersMap.get(channel));

		if (!globalResult && !channelResult) {
			LOG.warn("recevie mq message, bug has not mq listener to process. channel:" + channel + "  message:" + String.valueOf(message));
		}
	}

	private boolean notifyAll(String channel, Object message, Collection<MicroservicesmqMessageListener> listeners) {
		if (listeners == null || listeners.size() == 0) {
			return false;
		}

		if (syncRecevieMessageChannels.contains(channel)) {
			for (MicroservicesmqMessageListener listener : listeners) {
				try {
					listener.onMessage(channel, message);
				} catch (Throwable ex) {
					LOG.warn("listener[" + listener.getClass().getName() + "] execute mq message is error. channel:" + channel + "  message:" + String.valueOf(message));
				}
			}
		} else {
			for (MicroservicesmqMessageListener listener : listeners) {
				threadPool.execute(() -> {
					listener.onMessage(channel, message);
				});
			}
		}

		return true;
	}
}
