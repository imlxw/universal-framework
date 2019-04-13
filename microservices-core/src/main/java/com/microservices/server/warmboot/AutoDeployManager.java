package com.microservices.server.warmboot;

import java.io.File;

import com.jfinal.kit.LogKit;
import com.jfinal.kit.PathKit;

public class AutoDeployManager {

	private static AutoDeployManager manager = new AutoDeployManager();

	private AutoDeployManager() {
	}

	public static AutoDeployManager me() {
		return manager;
	}

	public void run() {

		File file = new File(PathKit.getRootClassPath());
		MicroservicesFileScanner scanner = new MicroservicesFileScanner(file.getAbsolutePath(), 3) {
			@Override
			public void onChange(String action, String file) {
				try {
					// System.err.println("file changes : " + file);
					// Microservices.me().getServer().restart();
					// MicroservicesServerFactory.me().buildServer().start();
					// System.err.println("Loading complete.");
				} catch (Exception e) {
					System.err.println("Error reconfiguring/restarting webapp after change in watched files");
					LogKit.error(e.getMessage(), e);
				}
			}
		};

		scanner.start();
	}

}
