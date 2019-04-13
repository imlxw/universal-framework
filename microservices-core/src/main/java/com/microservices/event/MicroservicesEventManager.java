package com.microservices.event;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.jfinal.log.Log;
import com.microservices.Microservices;
import com.microservices.event.annotation.EventConfig;
import com.microservices.utils.ArrayUtils;
import com.microservices.utils.ClassKits;
import com.microservices.utils.ClassScanner;

public class MicroservicesEventManager {

	private final ExecutorService threadPool;
	private final Map<String, List<MicroservicesEventListener>> asyncListenerMap;
	private final Map<String, List<MicroservicesEventListener>> listenerMap;
	private static final Log log = Log.getLog(MicroservicesEventManager.class);

	private static MicroservicesEventManager manager;

	public MicroservicesEventManager() {
		threadPool = new ThreadPoolExecutor(0, Integer.MAX_VALUE, 60L, TimeUnit.SECONDS, new SynchronousQueue<Runnable>());
		asyncListenerMap = new ConcurrentHashMap<>();
		listenerMap = new ConcurrentHashMap<>();

		initListeners();
	}

	public static MicroservicesEventManager me() {
		if (manager == null) {
			manager = ClassKits.singleton(MicroservicesEventManager.class);
		}
		return manager;
	}

	private void initListeners() {
		List<Class<MicroservicesEventListener>> classes = ClassScanner.scanSubClass(MicroservicesEventListener.class, true);
		if (ArrayUtils.isNullOrEmpty(classes)) {
			return;
		}
		for (Class<MicroservicesEventListener> clazz : classes) {
			registerListener(clazz);
		}
	}

	public void unRegisterListener(Class<? extends MicroservicesEventListener> listenerClass) {

		deleteListner(listenerMap, listenerClass);
		deleteListner(asyncListenerMap, listenerClass);

		if (Microservices.me().isDevMode()) {
			System.out.println(String.format("listener[%s]-->>unRegisterListener.", listenerClass));
		}

	}

	private void deleteListner(Map<String, List<MicroservicesEventListener>> map, Class<? extends MicroservicesEventListener> listenerClass) {
		for (Map.Entry<String, List<MicroservicesEventListener>> entry : map.entrySet()) {
			MicroservicesEventListener deleteListener = null;
			for (MicroservicesEventListener listener : entry.getValue()) {
				if (listener.getClass() == listenerClass) {
					deleteListener = listener;
				}
			}
			if (deleteListener != null) {
				entry.getValue().remove(deleteListener);
			}
		}
	}

	public void registerListener(Class<? extends MicroservicesEventListener> listenerClass) {

		if (listenerClass == null) {
			return;
		}

		EventConfig listenerAnnotation = listenerClass.getAnnotation(EventConfig.class);
		if (listenerAnnotation == null) {
			log.warn("listenerClass[" + listenerAnnotation + "] resigter fail,because not use EventConfig annotation.");
			return;
		}

		String[] actions = listenerAnnotation.action();
		if (actions == null || actions.length == 0) {
			log.warn("listenerClass[" + listenerAnnotation + "] resigter fail, because action is null or blank.");
			return;
		}

		if (listenerHasRegisterBefore(listenerClass)) {
			return;
		}

		MicroservicesEventListener listener = ClassKits.newInstance(listenerClass);
		if (listener == null) {
			return;
		}

		for (String action : actions) {
			List<MicroservicesEventListener> list = null;
			if (listenerAnnotation.async()) {
				list = asyncListenerMap.get(action);
			} else {
				list = listenerMap.get(action);
			}
			if (null == list) {
				list = new ArrayList<>();
			}
			if (list.isEmpty() || !list.contains(listener)) {
				list.add(listener);
			}

			Collections.sort(list, new Comparator<MicroservicesEventListener>() {
				@Override
				public int compare(MicroservicesEventListener o1, MicroservicesEventListener o2) {
					EventConfig c1 = ClassKits.getUsefulClass(o1.getClass()).getAnnotation(EventConfig.class);
					EventConfig c2 = ClassKits.getUsefulClass(o2.getClass()).getAnnotation(EventConfig.class);
					return c1.weight() - c2.weight();
				}
			});

			if (listenerAnnotation.async()) {
				asyncListenerMap.put(action, list);
			} else {
				listenerMap.put(action, list);
			}
		}

		if (Microservices.me().isDevMode()) {
			System.out.println(String.format("listener[%s]-->>registered.", listener));
		}

	}

	private boolean listenerHasRegisterBefore(Class<? extends MicroservicesEventListener> listenerClass) {
		return findFromMap(listenerClass, listenerMap) || findFromMap(listenerClass, asyncListenerMap);
	}

	private boolean findFromMap(Class<? extends MicroservicesEventListener> listenerClass, Map<String, List<MicroservicesEventListener>> map) {
		for (Map.Entry<String, List<MicroservicesEventListener>> entry : map.entrySet()) {
			List<MicroservicesEventListener> listeners = entry.getValue();
			if (listeners == null || listeners.isEmpty()) {
				continue;
			}
			for (MicroservicesEventListener ml : listeners) {
				if (listenerClass == ml.getClass()) {
					return true;
				}
			}
		}
		return false;
	}

	public void pulish(final MicroservicesEvent event) {
		String action = event.getAction();

		List<MicroservicesEventListener> syncListeners = listenerMap.get(action);
		if (syncListeners != null && !syncListeners.isEmpty()) {
			invokeListeners(event, syncListeners);
		}

		List<MicroservicesEventListener> listeners = asyncListenerMap.get(action);
		if (listeners != null && !listeners.isEmpty()) {
			invokeListenersAsync(event, listeners);
		}

	}

	private void invokeListeners(final MicroservicesEvent event, List<MicroservicesEventListener> syncListeners) {
		for (final MicroservicesEventListener listener : syncListeners) {
			try {
				if (Microservices.me().isDevMode()) {
					System.out.println(String.format("listener[%s]-->>onEvent(%s)", listener, event));
				}
				listener.onEvent(event);
			} catch (Throwable e) {
				log.error(String.format("listener[%s] onEvent is error! ", listener.getClass()), e);
			}
		}
	}

	private void invokeListenersAsync(final MicroservicesEvent event, List<MicroservicesEventListener> listeners) {
		for (final MicroservicesEventListener listener : listeners) {
			threadPool.execute(() -> {
				try {
					if (Microservices.me().isDevMode()) {
						System.out.println(String.format("listener[%s]-->>onEvent(%s) in async", listener, event));
					}
					listener.onEvent(event);
				} catch (Throwable e) {
					log.error(String.format("listener[%s] onEvent is error! ", listener.getClass()), e);
				}
			});
		}
	}

}
