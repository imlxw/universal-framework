package com.microservices.component.shiro;

import com.jfinal.core.Controller;
import com.microservices.Microservices;
import com.microservices.component.shiro.processer.AuthorizeResult;
import com.microservices.utils.StringUtils;
import com.microservices.web.fixedinterceptor.FixedInvocation;

/**
 * @version V1.0
 */
public interface MicroservicesShiroInvokeListener {

	/**
	 * 通过这个方法，可以用来处理 jwt、sso 等和shiro的整合
	 *
	 * @param inv
	 */
	public void onInvokeBefore(FixedInvocation inv);

	/**
	 * 通过这个方法，可以用来自定义shiro 处理结果 和 错误逻辑
	 *
	 * @param inv
	 * @param result
	 */
	public void onInvokeAfter(FixedInvocation inv, AuthorizeResult result);

	public static final MicroservicesShiroInvokeListener DEFAULT = new MicroservicesShiroInvokeListener() {

		private MicroservicesShiroConfig config = Microservices.config(MicroservicesShiroConfig.class);

		@Override
		public void onInvokeBefore(FixedInvocation inv) {
			// do nothing
		}

		@Override
		public void onInvokeAfter(FixedInvocation inv, AuthorizeResult result) {
			if (result.isOk()) {
				inv.invoke();
				return;
			}

			int errorCode = result.getErrorCode();
			switch (errorCode) {
				case AuthorizeResult.ERROR_CODE_UNAUTHENTICATED:
					doProcessUnauthenticated(inv.getController());
					break;
				case AuthorizeResult.ERROR_CODE_UNAUTHORIZATION:
					doProcessuUnauthorization(inv.getController());
					break;
				default:
					inv.getController().renderError(404);
			}
		}

		public void doProcessUnauthenticated(Controller controller) {
			if (StringUtils.isBlank(config.getLoginUrl())) {
				controller.renderError(401);
				return;
			}
			controller.redirect(config.getLoginUrl());
		}

		public void doProcessuUnauthorization(Controller controller) {
			if (StringUtils.isBlank(config.getUnauthorizedUrl())) {
				controller.renderError(403);
				return;
			}
			controller.redirect(config.getUnauthorizedUrl());
		}

	};

}
