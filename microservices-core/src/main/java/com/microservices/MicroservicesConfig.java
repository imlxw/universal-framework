package com.microservices;

import com.microservices.config.annotation.PropertyConfig;
import com.microservices.web.MicroservicesAppConfig;

/**
 * Microservices 配置类
 */
@PropertyConfig(prefix = "microservices")
public class MicroservicesConfig {

	private String version = "2.0.0";
	private String mode = Microservices.MODE.DEV.getValue();
	private boolean bannerEnable = true;
	private String bannerFile = "banner.txt";
	private String jfinalConfig = MicroservicesAppConfig.class.getName();

	public String getVersion() {
		return version;
	}

	public String getMode() {
		return mode;
	}

	public void setMode(String mode) {
		this.mode = mode;
	}

	public boolean isBannerEnable() {
		return bannerEnable;
	}

	public void setBannerEnable(boolean bannerEnable) {
		this.bannerEnable = bannerEnable;
	}

	public String getBannerFile() {
		return bannerFile;
	}

	public void setBannerFile(String bannerFile) {
		this.bannerFile = bannerFile;
	}

	public String getJfinalConfig() {
		return jfinalConfig;
	}

	public void setJfinalConfig(String jfinalConfig) {
		this.jfinalConfig = jfinalConfig;
	}

	@Override
	public String toString() {
		return "MicroservicesConfig {" + "version='" + version + '\'' + ", mode='" + mode + '\'' + ", bannerEnable=" + bannerEnable + ", bannerFile='" + bannerFile + '\'' + ", jfinalConfig='" + jfinalConfig + '\'' + '}';
	}
}
