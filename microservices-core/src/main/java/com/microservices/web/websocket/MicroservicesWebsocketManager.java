package com.microservices.web.websocket;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.websocket.server.ServerEndpoint;

import com.microservices.Microservices;
import com.microservices.utils.ClassScanner;
import com.microservices.utils.StringUtils;
import com.microservices.web.MicroservicesWebConfig;

public class MicroservicesWebsocketManager {
	private static MicroservicesWebsocketManager manager = new MicroservicesWebsocketManager();

	private Set<String> websocketEndPointValues = new HashSet<>();
	private Set<Class> websocketEndPoints = new HashSet<>();
	private MicroservicesWebConfig config = Microservices.config(MicroservicesWebConfig.class);

	private MicroservicesWebsocketManager() {
		List<Class> endPointClasses = ClassScanner.scanClassByAnnotation(ServerEndpoint.class, false);
		if (endPointClasses != null && endPointClasses.size() != 0) {
			for (Class entry : endPointClasses) {
				ServerEndpoint serverEndpoint = (ServerEndpoint) entry.getAnnotation(ServerEndpoint.class);
				String value = serverEndpoint.value();
				if (!StringUtils.isBlank(value)) {
					websocketEndPoints.add(entry);
					websocketEndPointValues.add(value);
				}
			}
		}
	}

	public static MicroservicesWebsocketManager me() {
		return manager;
	}

	public boolean isWebsokcetEndPoint(String endPointValue) {
		if (!config.isWebsocketEnable()) {
			return false;
		}

		if (config.getWebsocketBasePath() != null) {
			return endPointValue.startsWith(config.getWebsocketBasePath());
		}

		if (websocketEndPoints.isEmpty()) {
			return false;
		}

		return websocketEndPointValues.contains(endPointValue);
	}

	public Set<String> getWebsocketEndPointValues() {
		return websocketEndPointValues;
	}

	public Set<Class> getWebsocketEndPoints() {
		return websocketEndPoints;
	}
}
