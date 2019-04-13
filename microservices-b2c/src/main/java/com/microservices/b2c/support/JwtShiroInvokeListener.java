package com.microservices.b2c.support;

import com.jfinal.core.Controller;
import com.jfinal.log.Log;
import com.microservices.admin.base.common.RestResult;
import com.microservices.admin.base.plugin.jwt.shiro.JwtAuthenticationToken;
import com.microservices.component.jwt.JwtManager;
import com.microservices.component.shiro.MicroservicesShiroInvokeListener;
import com.microservices.component.shiro.processer.AuthorizeResult;
import com.microservices.utils.StringUtils;
import com.microservices.web.controller.MicroservicesController;
import com.microservices.web.fixedinterceptor.FixedInvocation;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.subject.Subject;

import java.util.Map;

/**
 * jwt shiro listener
 */
public class JwtShiroInvokeListener implements MicroservicesShiroInvokeListener {

	private final static Log log = Log.getLog(JwtShiroInvokeListener.class);

	@Override
	public void onInvokeBefore(FixedInvocation inv) {
		MicroservicesController controller = (MicroservicesController) inv.getController();
		String jwtToken = controller.getHeader(JwtManager.me().getHttpHeaderName());

		if (StringUtils.isBlank(jwtToken)) {
			inv.invoke();
			return;
		}

		Map jwtParas = JwtManager.me().getParas();
		String userId = String.valueOf(jwtParas.get("userId"));

		AuthenticationToken token = new JwtAuthenticationToken(userId, jwtToken);

		try {
			Subject subject = SecurityUtils.getSubject();
			subject.login(token);
		} catch (Exception e) {
			log.error(e.getMessage());
		}
	}

	@Override
	public void onInvokeAfter(FixedInvocation inv, AuthorizeResult result) {
		if (result == null || result.isOk()) {
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
				doProcessuDefault(inv.getController());
		}
	}

	/**
	 * 其他处理
	 * 
	 * @param controller
	 */
	private void doProcessuDefault(Controller controller) {
		controller.renderJson(RestResult.buildError("404"));
	}

	/**
	 * 没有认证信息处理
	 * 
	 * @param controller
	 */
	private void doProcessUnauthenticated(Controller controller) {
		controller.renderJson(RestResult.buildError("401"));
	}

	/**
	 * 无授权信息处理
	 * 
	 * @param controller
	 */
	private void doProcessuUnauthorization(Controller controller) {
		controller.renderJson(RestResult.buildError("403"));
	}
}
