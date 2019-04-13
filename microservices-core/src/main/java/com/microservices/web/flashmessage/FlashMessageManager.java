package com.microservices.web.flashmessage;

import java.util.HashMap;
import java.util.Map;

import com.jfinal.core.Controller;
import com.microservices.web.controller.MicroservicesController;

/**
 * @version V1.0
 * @Title: 用于对 FlashMessage 的管理
 * @Package com.microservices.web.flashmessage
 */
public class FlashMessageManager {

	private static final String FLASH_SESSION_ATTR = "_JFM_"; // JFM : microservices flash message

	private static final FlashMessageManager ME = new FlashMessageManager();

	public static FlashMessageManager me() {
		return ME;
	}

	public void renderTo(Controller controller) {
		HashMap<String, Object> flash = controller.getSessionAttr(FLASH_SESSION_ATTR);
		if (flash == null || flash.isEmpty()) {
			return;
		}
		for (Map.Entry<String, Object> entry : flash.entrySet()) {
			controller.setAttr(entry.getKey(), entry.getValue());
		}
	}

	public void init(Controller controller) {
		if (!(controller instanceof MicroservicesController)) {
			return;
		}
		HashMap flash = ((MicroservicesController) controller).getFlashAttrs();
		if (flash == null || flash.isEmpty()) {
			return;
		}
		controller.setSessionAttr(FLASH_SESSION_ATTR, flash);
	}

	public void release(Controller controller) {
		if (controller.getSessionAttr(FLASH_SESSION_ATTR) == null) {
			return;
		}
		controller.removeSessionAttr(FLASH_SESSION_ATTR);
	}

}
