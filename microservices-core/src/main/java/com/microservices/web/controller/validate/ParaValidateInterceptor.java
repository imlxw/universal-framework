package com.microservices.web.controller.validate;

import com.jfinal.core.Controller;
import com.jfinal.kit.Ret;
import com.microservices.utils.ArrayUtils;
import com.microservices.utils.RequestUtils;
import com.microservices.utils.StringUtils;
import com.microservices.web.controller.MicroservicesController;
import com.microservices.web.fixedinterceptor.FixedInterceptor;
import com.microservices.web.fixedinterceptor.FixedInvocation;

/**
 * 验证拦截器
 */
public class ParaValidateInterceptor implements FixedInterceptor {

	public static final int DEFAULT_ERROR_CODE = 99;

	@Override
	public void intercept(FixedInvocation inv) {

		EmptyValidate emptyParaValidate = inv.getMethod().getAnnotation(EmptyValidate.class);
		if (emptyParaValidate != null && !validateEmpty(inv, emptyParaValidate)) {
			return;
		}

		CaptchaValidate captchaValidate = inv.getMethod().getAnnotation(CaptchaValidate.class);
		if (captchaValidate != null && !validateCaptache(inv, captchaValidate)) {
			return;
		}

		inv.invoke();

	}

	/**
	 * 对验证码进行验证
	 *
	 * @param inv
	 * @param captchaValidate
	 * @return
	 */
	private boolean validateCaptache(FixedInvocation inv, CaptchaValidate captchaValidate) {
		String formName = captchaValidate.form();
		if (StringUtils.isBlank(formName)) {
			throw new IllegalArgumentException("@CaptchaValidate.form must not be empty in " + inv.getController().getClass().getName() + "." + inv.getMethodName());
		}

		Controller controller = inv.getController();
		if (controller.validateCaptcha(formName)) {
			return true;
		}

		switch (captchaValidate.renderType()) {
			case ValidateRenderType.DEFAULT:
				if (RequestUtils.isAjaxRequest(controller.getRequest())) {
					controller.renderJson(Ret.fail("message", captchaValidate.flashMessage()).set("code", DEFAULT_ERROR_CODE).set("form", formName));
				} else {
					controller.renderError(404);
				}
				break;
			case ValidateRenderType.JSON:
				controller.renderJson(Ret.fail("message", captchaValidate.message()).set("code", DEFAULT_ERROR_CODE).set("form", formName));
				break;
			case ValidateRenderType.REDIRECT:
				if (controller instanceof MicroservicesController) {
					((MicroservicesController) controller).setFlashAttr("message", captchaValidate.flashMessage());
				}
				controller.redirect(captchaValidate.message());
				break;
			case ValidateRenderType.RENDER:
				controller.render(captchaValidate.message());
				break;
			case ValidateRenderType.TEXT:
				controller.renderText(captchaValidate.message());
				break;
			default:
				throw new IllegalArgumentException("can not process render  : " + captchaValidate.renderType());
		}

		return false;
	}

	/**
	 * 非空判断验证
	 *
	 * @param inv
	 * @param emptyParaValidate
	 * @return
	 */
	private boolean validateEmpty(FixedInvocation inv, EmptyValidate emptyParaValidate) {
		Form[] forms = emptyParaValidate.value();
		if (ArrayUtils.isNullOrEmpty(forms)) {
			return true;
		}

		for (Form form : forms) {
			String formName = form.name();
			if (StringUtils.isBlank(formName)) {
				throw new IllegalArgumentException("@Form.value must not be empty in " + inv.getController().getClass().getName() + "." + inv.getMethodName());
			}
			String value = inv.getController().getPara(formName);
			if (value == null || value.trim().length() == 0) {
				renderError(inv.getController(), formName, form.message(), emptyParaValidate);
				return false;
			}
		}

		return true;
	}

	private void renderError(Controller controller, String formName, String message, EmptyValidate emptyParaValidate) {
		switch (emptyParaValidate.renderType()) {
			case ValidateRenderType.DEFAULT:
				if (RequestUtils.isAjaxRequest(controller.getRequest())) {
					controller.renderJson(Ret.fail("message", message).set("code", DEFAULT_ERROR_CODE).set("form", formName));
				} else {
					controller.renderError(404);
				}
				break;
			case ValidateRenderType.JSON:
				controller.renderJson(Ret.fail("message", message).set("code", DEFAULT_ERROR_CODE).set("form", formName));
				break;
			case ValidateRenderType.REDIRECT:
				if (controller instanceof MicroservicesController) {
					((MicroservicesController) controller).setFlashAttr("message", message);
				}
				controller.redirect(emptyParaValidate.message());
				break;
			case ValidateRenderType.RENDER:
				controller.render(emptyParaValidate.message());
				break;
			case ValidateRenderType.TEXT:
				controller.renderText(emptyParaValidate.message());
				break;
			default:
				throw new IllegalArgumentException("can not process render : " + emptyParaValidate.renderType());
		}
	}

}
