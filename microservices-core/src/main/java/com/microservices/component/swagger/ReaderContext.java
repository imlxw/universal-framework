package com.microservices.component.swagger;

import java.util.ArrayList;
import java.util.List;

import io.swagger.models.Swagger;
import io.swagger.models.parameters.Parameter;

/**
 * The <code>ReaderContext</code> class is wrapper for the <code>Reader</code> parameters.
 */
public class ReaderContext {

	private Swagger swagger;
	private Class<?> cls;
	private String parentPath;
	private String parentHttpMethod;
	private boolean readHidden;
	private List<String> parentConsumes = new ArrayList<>();
	private List<String> parentProduces = new ArrayList<>();
	private List<String> parentTags = new ArrayList<>();
	private List<Parameter> parentParameters = new ArrayList<>();

	public ReaderContext(Class<?> cls, String parentPath, String parentHttpMethod, boolean readHidden) {
		setCls(cls);
		setParentPath(parentPath);
		setParentHttpMethod(parentHttpMethod);
		setReadHidden(readHidden);
	}

	public Class<?> getCls() {
		return cls;
	}

	public void setCls(Class<?> cls) {
		this.cls = cls;
	}

	public String getParentPath() {
		return parentPath;
	}

	public void setParentPath(String parentPath) {
		this.parentPath = parentPath;
	}

	public String getParentHttpMethod() {
		return parentHttpMethod;
	}

	public void setParentHttpMethod(String parentHttpMethod) {
		this.parentHttpMethod = parentHttpMethod;
	}

	public boolean isReadHidden() {
		return readHidden;
	}

	public void setReadHidden(boolean readHidden) {
		this.readHidden = readHidden;
	}

	public List<String> getParentConsumes() {
		return parentConsumes;
	}

	public void setParentConsumes(List<String> parentConsumes) {
		this.parentConsumes = parentConsumes;
	}

	public List<String> getParentProduces() {
		return parentProduces;
	}

	public void setParentProduces(List<String> parentProduces) {
		this.parentProduces = parentProduces;
	}

	public List<String> getParentTags() {
		return parentTags;
	}

	public void setParentTags(List<String> parentTags) {
		this.parentTags = parentTags;
	}

	public List<Parameter> getParentParameters() {
		return parentParameters;
	}

	public void setParentParameters(List<Parameter> parentParameters) {
		this.parentParameters = parentParameters;
	}
}
