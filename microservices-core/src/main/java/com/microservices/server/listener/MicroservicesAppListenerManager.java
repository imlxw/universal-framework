package com.microservices.server.listener;

import java.util.ArrayList;
import java.util.List;

import com.google.inject.Binder;
import com.jfinal.config.Constants;
import com.jfinal.config.Interceptors;
import com.jfinal.config.Routes;
import com.jfinal.log.Log;
import com.jfinal.template.Engine;
import com.microservices.aop.jfinal.JfinalHandlers;
import com.microservices.aop.jfinal.JfinalPlugins;
import com.microservices.server.ContextListeners;
import com.microservices.server.MicroservicesServer;
import com.microservices.server.Servlets;
import com.microservices.utils.ClassKits;
import com.microservices.utils.ClassScanner;
import com.microservices.web.fixedinterceptor.FixedInterceptors;

public class MicroservicesAppListenerManager implements MicroservicesAppListener {
	private static Log log = Log.getLog(MicroservicesAppListenerManager.class);

	private static MicroservicesAppListenerManager me = new MicroservicesAppListenerManager();

	public static MicroservicesAppListenerManager me() {
		return me;
	}

	List<MicroservicesAppListener> listeners = new ArrayList<>();

	private MicroservicesAppListenerManager() {
		List<Class<MicroservicesAppListener>> allListeners = ClassScanner.scanSubClass(MicroservicesAppListener.class, true);
		if (allListeners == null || allListeners.size() == 0) {
			return;
		}

		for (Class<? extends MicroservicesAppListener> clazz : allListeners) {
			if (MicroservicesAppListenerManager.class == clazz || MicroservicesAppListenerBase.class == clazz) {
				continue;
			}

			MicroservicesAppListener listener = ClassKits.newInstance(clazz, false);
			if (listener != null) {
				listeners.add(listener);
			}
		}
	}

	@Override
	public void onMicroservicesDeploy(Servlets servlets, ContextListeners listeners) {
		for (MicroservicesAppListener listener : this.listeners) {
			try {
				listener.onMicroservicesDeploy(servlets, listeners);
			} catch (Throwable ex) {
				log.error(ex.toString(), ex);
			}
		}
	}

	@Override
	public void onJfinalConstantConfig(Constants constants) {
		for (MicroservicesAppListener listener : listeners) {
			try {
				listener.onJfinalConstantConfig(constants);
			} catch (Throwable ex) {
				log.error(ex.toString(), ex);
			}
		}
	}

	@Override
	public void onJfinalRouteConfig(Routes routes) {
		for (MicroservicesAppListener listener : listeners) {
			try {
				listener.onJfinalRouteConfig(routes);
			} catch (Throwable ex) {
				log.error(ex.toString(), ex);
			}
		}
	}

	@Override
	public void onJfinalEngineConfig(Engine engine) {
		for (MicroservicesAppListener listener : listeners) {
			try {
				listener.onJfinalEngineConfig(engine);
			} catch (Throwable ex) {
				log.error(ex.toString(), ex);
			}
		}
	}

	@Override
	public void onJfinalPluginConfig(JfinalPlugins plugins) {
		for (MicroservicesAppListener listener : listeners) {
			try {
				listener.onJfinalPluginConfig(plugins);
			} catch (Throwable ex) {
				log.error(ex.toString(), ex);
			}
		}
	}

	@Override
	public void onInterceptorConfig(Interceptors interceptors) {
		for (MicroservicesAppListener listener : listeners) {
			try {
				listener.onInterceptorConfig(interceptors);
			} catch (Throwable ex) {
				log.error(ex.toString(), ex);
			}
		}
	}

	@Override
	public void onFixedInterceptorConfig(FixedInterceptors fixedInterceptors) {
		for (MicroservicesAppListener listener : listeners) {
			try {
				listener.onFixedInterceptorConfig(fixedInterceptors);
			} catch (Throwable ex) {
				log.error(ex.toString(), ex);
			}
		}
	}

	@Override
	public void onHandlerConfig(JfinalHandlers handlers) {
		for (MicroservicesAppListener listener : listeners) {
			try {
				listener.onHandlerConfig(handlers);
			} catch (Throwable ex) {
				log.error(ex.toString(), ex);
			}
		}
	}

	@Override
	public void onJFinalStarted() {
		for (MicroservicesAppListener listener : listeners) {
			try {
				listener.onJFinalStarted();
			} catch (Throwable ex) {
				log.error(ex.toString(), ex);
			}
		}
	}

	@Override
	public void onJFinalStop() {
		for (MicroservicesAppListener listener : listeners) {
			try {
				listener.onJFinalStop();
			} catch (Throwable ex) {
				log.error(ex.toString(), ex);
			}
		}
	}

	@Override
	public void onMicroservicesStarted() {
		for (MicroservicesAppListener listener : listeners) {
			try {
				listener.onMicroservicesStarted();
			} catch (Throwable ex) {
				log.error(ex.toString(), ex);
			}
		}
	}

	@Override
	public void onAppStartBefore(MicroservicesServer microservicesServer) {
		for (MicroservicesAppListener listener : listeners) {
			try {
				listener.onAppStartBefore(microservicesServer);
			} catch (Throwable ex) {
				log.error(ex.toString(), ex);
			}
		}
	}

	@Override
	public void onGuiceConfigure(Binder binder) {
		for (MicroservicesAppListener listener : listeners) {
			try {
				listener.onGuiceConfigure(binder);
			} catch (Throwable ex) {
				log.error(ex.toString(), ex);
			}
		}
	}
}
