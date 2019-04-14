package com.microservices.wechat.interceptor;

import javax.servlet.http.HttpServletRequest;

import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;
import com.jfinal.core.JFinal;
import com.jfinal.weixin.sdk.api.ApiConfigKit;
import com.jfinal.weixin.sdk.api.ApiResult;
import com.microservices.utils.StringUtils;
import com.microservices.wechat.controller.MicroservicesWechatController;

/**
 * 获取用户信息的连接器。 相关文档：https://mp.weixin.qq.com/wiki/17/c0f37d5704f0b64713d5d2c37b468d75.html
 */
public class WechatUserInterceptor implements Interceptor {

	public static final String AUTHORIZE_URL = "https://open.weixin.qq.com/connect/oauth2/authorize" + "?appid={appid}" + "&redirect_uri={redirecturi}" + "&response_type=code" + "&scope=snsapi_userinfo" + "&state=235#wechat_redirect";

	public static final String BASE_AUTHORIZE_URL = "https://open.weixin.qq.com/connect/oauth2/authorize" + "?appid={appid}" + "&redirect_uri={redirecturi}" + "&response_type=code" + "&scope=snsapi_base" + "&state=235#wechat_redirect";

	@Override
	public void intercept(Invocation inv) {

		MicroservicesWechatController controller = (MicroservicesWechatController) inv.getController();

		/**
		 * 是否允许访问，默认情况下只有是微信浏览器允许访问
		 */
		if (controller.isAllowVisit()) {
			doIntercept(inv);
			return;
		}

		controller.doNotAlloVisitRedirect();

	}

	private void doIntercept(Invocation inv) {

		MicroservicesWechatController controller = (MicroservicesWechatController) inv.getController();

		/**
		 * isFromBaseScope 表示 静默授权过来的 静默授权过来是无法获取用户的基本信息的，只能获得openid， 这个时候，只能查看openid在数据库里是否已经存在， 如果存在，则已经标示到了当前用户了 如果不存在，则通过snsapi_userinfo方式去获取，这样就可以获取到全部资料，保存到数据库
		 */
		boolean isFromBaseScope = isFromBaseScope(inv);

		if (isFromBaseScope) {

			String openid = inv.getController().getSessionAttr(MicroservicesWechatController.SESSION_WECHAT_OPEN_ID);
			Object user = controller.doGetUserByOpenId(openid);
			if (user != null) {
				controller.setAttr(MicroservicesWechatController.ATTR_USER_OBJECT, user);
				inv.invoke();
				return;
			}
		}

		String wechatUserJson = controller.getSessionAttr(MicroservicesWechatController.SESSION_WECHAT_USER_JSON);
		if (validateUserJson(wechatUserJson)) {
			Object user = controller.doSaveOrUpdateUserByApiResult(ApiResult.create(wechatUserJson));
			if (user == null) {
				controller.renderText("can not save or update user when get user from wechat");
				return;
			}
			controller.setAttr(MicroservicesWechatController.ATTR_USER_OBJECT, user);
			inv.invoke();
			return;
		}

		// 移除脏数据后，再次进入授权页面
		controller.clearWechatSession();

		String appid = ApiConfigKit.getAppId();

		HttpServletRequest request = controller.getRequest();
		// 获取用户将要去的路径
		String queryString = request.getQueryString();

		// 被拦截前的请求URL
		String toUrl = request.getRequestURI();
		if (StringUtils.isNotBlank(queryString)) {
			toUrl = toUrl.concat("?").concat(queryString);
		}

		String controllerKey = inv.getControllerKey();
		String callbackControllerKey = controllerKey + "/wechatCallback";

		if (!JFinal.me().getAllActionKeys().contains(callbackControllerKey)) {
			callbackControllerKey = controllerKey.substring(0, controllerKey.lastIndexOf("/")) + "/wechatCallback";
		}

		String redirectUrl = controller.getBaseUrl() + callbackControllerKey + "?goto=" + StringUtils.urlEncode(toUrl);

		redirectUrl = StringUtils.urlEncode(redirectUrl);
		String authUrl = isFromBaseScope ? AUTHORIZE_URL : BASE_AUTHORIZE_URL;
		String url = authUrl.replace("{redirecturi}", redirectUrl).replace("{appid}", appid.trim());
		controller.redirect(url);
	}

	/**
	 * 验证微信用户的json信息是否正确
	 *
	 * @param wechatUserJson
	 * @return
	 */
	protected boolean validateUserJson(String wechatUserJson) {
		return StringUtils.isNotBlank(wechatUserJson) && wechatUserJson.contains("openid") && wechatUserJson.contains("nickname") // 包含昵称
				&& wechatUserJson.contains("headimgurl"); // 包含头像
	}

	protected boolean isFromBaseScope(Invocation inv) {
		String scope = inv.getController().getSessionAttr(MicroservicesWechatController.SESSION_WECHAT_SCOPE);
		return scope != null && "snsapi_base".equalsIgnoreCase(scope);
	}

}
