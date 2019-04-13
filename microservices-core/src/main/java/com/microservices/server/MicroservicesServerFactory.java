package com.microservices.server;

import com.microservices.Microservices;
import com.microservices.server.jetty.JettyServer;
import com.microservices.server.tomcat.TomcatServer;
import com.microservices.server.undertow.UnderTowServer;

public class MicroservicesServerFactory {

	private static MicroservicesServerFactory me = new MicroservicesServerFactory();

	public static MicroservicesServerFactory me() {
		return me;
	}

	public MicroservicesServer buildServer() {

		MicroservicesServerConfig microservicesServerConfig = Microservices.config(MicroservicesServerConfig.class);

		switch (microservicesServerConfig.getType()) {
			case MicroservicesServerConfig.TYPE_UNDERTOW:
				return new UnderTowServer();
			case MicroservicesServerConfig.TYPE_TOMCAT:
				return new TomcatServer();
			case MicroservicesServerConfig.TYPE_JETTY:
				return new JettyServer();
			default:
				return new UnderTowServer();
		}
	}

}
