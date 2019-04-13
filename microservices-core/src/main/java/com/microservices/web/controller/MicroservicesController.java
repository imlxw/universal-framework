package com.microservices.web.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.jfinal.aop.Before;
import com.jfinal.core.Controller;
import com.jfinal.ext.interceptor.NotAction;
import com.jfinal.kit.HttpKit;
import com.jfinal.upload.UploadFile;
import com.microservices.component.jwt.JwtManager;
import com.microservices.utils.ArrayUtils;
import com.microservices.utils.RequestUtils;

public class MicroservicesController extends Controller {

	private static final Object NULL_OBJ = new Object();
	private static final String BODY_STRING_ATTR = "__body_str";

	/**
	 * 是否是手机浏览器
	 *
	 * @return
	 */
	@Before(NotAction.class)
	public boolean isMoblieBrowser() {
		return RequestUtils.isMoblieBrowser(getRequest());
	}

	/**
	 * 是否是微信浏览器
	 *
	 * @return
	 */
	@Before(NotAction.class)
	public boolean isWechatBrowser() {
		return RequestUtils.isWechatBrowser(getRequest());
	}

	/**
	 * 是否是IE浏览器
	 *
	 * @return
	 */
	@Before(NotAction.class)
	public boolean isIEBrowser() {
		return RequestUtils.isIEBrowser(getRequest());
	}

	/**
	 * 是否是ajax请求
	 *
	 * @return
	 */
	@Before(NotAction.class)
	public boolean isAjaxRequest() {
		return RequestUtils.isAjaxRequest(getRequest());
	}

	/**
	 * 是否是multpart的请求（带有文件上传的请求）
	 *
	 * @return
	 */
	@Before(NotAction.class)
	public boolean isMultipartRequest() {
		return RequestUtils.isMultipartRequest(getRequest());
	}

	/**
	 * 获取ip地址
	 *
	 * @return
	 */
	@Before(NotAction.class)
	public String getIPAddress() {
		return RequestUtils.getIpAddress(getRequest());
	}

	/**
	 * 获取 referer
	 *
	 * @return
	 */
	@Before(NotAction.class)
	public String getReferer() {
		return RequestUtils.getReferer(getRequest());
	}

	/**
	 * 获取ua
	 *
	 * @return
	 */
	@Before(NotAction.class)
	public String getUserAgent() {
		return RequestUtils.getUserAgent(getRequest());
	}

	protected HashMap<String, Object> flash;

	@Before(NotAction.class)
	public Controller setFlashAttr(String name, Object value) {
		if (flash == null) {
			flash = new HashMap<>();
		}

		flash.put(name, value);
		return this;
	}

	@Before(NotAction.class)
	public Controller setFlashMap(Map map) {
		if (map == null) {
			throw new NullPointerException("map is null");
		}
		if (flash == null) {
			flash = new HashMap<>();
		}

		flash.putAll(map);
		return this;
	}

	@Before(NotAction.class)
	public <T> T getFlashAttr(String name) {
		return flash == null ? null : (T) flash.get(name);
	}

	@Before(NotAction.class)
	public HashMap<String, Object> getFlashAttrs() {
		return flash;
	}

	private HashMap<String, Object> jwtMap;

	@Before(NotAction.class)
	public Controller setJwtAttr(String name, Object value) {
		if (jwtMap == null) {
			jwtMap = new HashMap<>();
		}

		jwtMap.put(name, value);
		return this;
	}

	@Before(NotAction.class)
	public Controller setJwtMap(Map map) {
		if (map == null) {
			throw new NullPointerException("map is null");
		}
		if (jwtMap == null) {
			jwtMap = new HashMap<>();
		}

		jwtMap.putAll(map);
		return this;
	}

	@Before(NotAction.class)
	public <T> T getJwtAttr(String name) {
		return jwtMap == null ? null : (T) jwtMap.get(name);
	}

	@Before(NotAction.class)
	public HashMap<String, Object> getJwtAttrs() {
		return jwtMap;
	}

	@Before(NotAction.class)
	public <T> T getJwtPara(String name) {
		return JwtManager.me().getPara(name);
	}

	@Before(NotAction.class)
	public Map getJwtParas() {
		return JwtManager.me().getParas();
	}

	@Before(NotAction.class)
	public String createJwtToken() {
		if (jwtMap == null) {
			throw new NullPointerException("jwt attrs is null");
		}
		return JwtManager.me().createJwtToken(jwtMap);
	}

	/**
	 * 获取当前网址
	 *
	 * @return
	 */
	@Before(NotAction.class)
	public String getBaseUrl() {
		HttpServletRequest req = getRequest();
		int port = req.getServerPort();

		return port == 80 ? String.format("%s://%s%s", req.getScheme(), req.getServerName(), req.getContextPath()) : String.format("%s://%s%s%s", req.getScheme(), req.getServerName(), ":" + port, req.getContextPath());

	}

	@Before(NotAction.class)
	public String getBodyString() {
		Object object = getAttr(BODY_STRING_ATTR);
		if (object == NULL_OBJ) {
			return null;
		}

		if (object != null) {
			return (String) object;
		}

		object = HttpKit.readData(getRequest());
		if (object == null) {
			setAttr(BODY_STRING_ATTR, NULL_OBJ);
		} else {
			setAttr(BODY_STRING_ATTR, object);
		}

		return (String) object;
	}

	/**
	 * 获取所有上传的文件
	 *
	 * @return
	 */
	@Before(NotAction.class)
	public HashMap<String, UploadFile> getUploadFilesMap() {
		if (!isMultipartRequest()) {
			return null;
		}

		List<UploadFile> fileList = getFiles();
		HashMap<String, UploadFile> filesMap = null;
		if (ArrayUtils.isNotEmpty(fileList)) {
			filesMap = new HashMap<String, UploadFile>();
			for (UploadFile ufile : fileList) {
				filesMap.put(ufile.getParameterName(), ufile);
			}
		}
		return filesMap;
	}

}
