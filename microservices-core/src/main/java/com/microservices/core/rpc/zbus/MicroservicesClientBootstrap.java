package com.microservices.core.rpc.zbus;

import io.zbus.mq.Broker;
import io.zbus.rpc.RpcConfig;
import io.zbus.rpc.RpcInvoker;
import io.zbus.rpc.bootstrap.mq.ClientBootstrap;
import io.zbus.rpc.transport.mq.RpcMessageInvoker;
import io.zbus.transport.ServerAddress;

/**
 * @version V1.0
 * @Package com.microservices.core.rpc.zbus
 */
public class MicroservicesClientBootstrap extends ClientBootstrap {

	public <T> T serviceObtain(Class<T> serviceClass, String group, String version) {

		if (broker == null) {
			String token = producerConfig.getToken();
			if (token != null) {
				for (ServerAddress address : brokerConfig.getTrackerList()) {
					if (address.getToken() == null) {
						address.setToken(token);
					}
				}
			}
			broker = new Broker(brokerConfig);
		}
		producerConfig.setBroker(broker);
		RpcMessageInvoker messageInvoker = new RpcMessageInvoker(producerConfig, this.topic);

		RpcConfig rpcConfig = new RpcConfig();
		rpcConfig.setModule(ZbusKits.buildModule(serviceClass, group, version));
		rpcConfig.setMessageInvoker(messageInvoker);

		RpcInvoker rpcInvoker = new RpcInvoker(rpcConfig);
		rpcInvoker.getCodec().setRequestTypeInfo(requestTypeInfo);

		return rpcInvoker.createProxy(serviceClass, rpcConfig);
	}

	// public RpcInvoker invoker(Class serviceClass, String group, String version) {
	// if (broker == null) {
	// String token = producerConfig.getToken();
	// if (token != null) {
	// for (ServerAddress address : brokerConfig.getTrackerList()) {
	// if (address.getToken() == null) {
	// address.setToken(token);
	// }
	// }
	// }
	// broker = new Broker(brokerConfig);
	// }
	// producerConfig.setBroker(broker);
	// RpcMessageInvoker messageInvoker = new RpcMessageInvoker(producerConfig, this.topic);
	//
	// RpcConfig rpcConfig = new RpcConfig();
	// rpcConfig.setModule(ZbusKits.buildModule(serviceClass, group, version));
	// rpcConfig.setMessageInvoker(messageInvoker);
	//
	// RpcInvoker rpcInvoker = new RpcInvoker(rpcConfig);
	// rpcInvoker.getCodec().setRequestTypeInfo(requestTypeInfo);
	//
	// return rpcInvoker;
	// }

}
