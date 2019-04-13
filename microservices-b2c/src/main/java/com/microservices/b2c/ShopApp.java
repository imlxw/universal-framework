package com.microservices.b2c;

import com.microservices.Microservices;

/**
 * 服务启动入口(不需要启动，供扩展)
 */
public class ShopApp {
	public static void main(String[] args) {
		Microservices.run(args);
	}
}
