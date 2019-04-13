package com.microservices.core.rpc;

import com.microservices.Microservices;

public abstract class MicroservicesrpcBase implements Microservicesrpc {

	private MicroservicesrpcConfig rpcConfig = Microservices.config(MicroservicesrpcConfig.class);

	public MicroservicesrpcConfig getRpcConfig() {
		return rpcConfig;
	}

	@Override
	public void onInitBefore() {

	}

	@Override
	public void onInited() {

	}
}
