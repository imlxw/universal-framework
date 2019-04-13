package com.microservices.server.tomcat;

import com.microservices.exception.MicroservicesException;
import com.microservices.server.MicroservicesServer;

public class TomcatServer extends MicroservicesServer {

	@Override
	public boolean start() {
		new MicroservicesException("tomcat server not finish!!!");
		return false;
	}

	@Override
	public boolean restart() {
		return false;
	}

	@Override
	public boolean stop() {
		return false;
	}
}
