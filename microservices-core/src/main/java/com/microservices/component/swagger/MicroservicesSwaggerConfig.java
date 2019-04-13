package com.microservices.component.swagger;

import com.microservices.config.annotation.PropertyConfig;
import com.microservices.utils.StringUtils;

@PropertyConfig(prefix = "microservices.swagger")
public class MicroservicesSwaggerConfig {

	private String path;

	private String title;
	private String description;
	private String version;
	private String termsOfService;
	private String host;

	private String contactName;
	private String contactEmail;
	private String contactUrl;

	private String licenseName;
	private String licenseUrl;

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public boolean isConfigOk() {
		return StringUtils.isNotBlank(path);
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getTermsOfService() {
		return termsOfService;
	}

	public void setTermsOfService(String termsOfService) {
		this.termsOfService = termsOfService;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public String getContactName() {
		return contactName;
	}

	public void setContactName(String contactName) {
		this.contactName = contactName;
	}

	public String getContactEmail() {
		return contactEmail;
	}

	public void setContactEmail(String contactEmail) {
		this.contactEmail = contactEmail;
	}

	public String getContactUrl() {
		return contactUrl;
	}

	public void setContactUrl(String contactUrl) {
		this.contactUrl = contactUrl;
	}

	public String getLicenseName() {
		return licenseName;
	}

	public void setLicenseName(String licenseName) {
		this.licenseName = licenseName;
	}

	public String getLicenseUrl() {
		return licenseUrl;
	}

	public void setLicenseUrl(String licenseUrl) {
		this.licenseUrl = licenseUrl;
	}
}
