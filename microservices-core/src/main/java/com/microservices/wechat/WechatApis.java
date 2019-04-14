package com.microservices.wechat;

import java.util.concurrent.Callable;

import com.jfinal.kit.Kv;
import com.jfinal.kit.StrKit;
import com.jfinal.weixin.sdk.api.AccessTokenApi;
import com.jfinal.weixin.sdk.api.ApiConfigKit;
import com.jfinal.weixin.sdk.api.ApiResult;
import com.jfinal.weixin.sdk.api.JsTicket;
import com.jfinal.weixin.sdk.api.MenuApi;
import com.jfinal.weixin.sdk.kit.ParaMap;
import com.jfinal.weixin.sdk.utils.HttpUtils;
import com.jfinal.weixin.sdk.utils.RetryUtils;
import com.microservices.Microservices;

public class WechatApis {

	public static ApiResult createMenu(String jsonString) {
		return MenuApi.createMenu(jsonString);
	}

	/**
	 * 网页授权获取用户信息，必须是最新的token，才能获得完整的用户资料
	 *
	 * @param token
	 * @param openId
	 * @return
	 */
	public static ApiResult getUserInfo(String token, String openId) {
		Kv pm = Kv.by("access_token", token).set("openid", openId).set("lang", "zh_CN");
		String jsonResult = Microservices.httpGet("https://api.weixin.qq.com/sns/userinfo", pm);

		if (jsonResult == null)
			return null;

		return new ApiResult(jsonResult);
	}

	/**
	 * 获取微信的openId
	 *
	 * @param code
	 * @return
	 */
	public static ApiResult getAccessTokenAndOpenId(String code) {

		String url = "https://api.weixin.qq.com/sns/oauth2/access_token?" + "appid={appid}" + "&secret={secret}" + "&code={code}" + "&grant_type=authorization_code";

		String getOpenIdUrl = url.replace("{appid}", ApiConfigKit.getAppId()).replace("{secret}", ApiConfigKit.getApiConfig().getAppSecret()).replace("{code}", code);

		String jsonResult = Microservices.httpGet(getOpenIdUrl);

		if (jsonResult == null)
			return null;

		return new ApiResult(jsonResult);
	}

	public enum JsApiType {
		jsapi, wx_card
	}

	private static String apiUrl = "https://api.weixin.qq.com/cgi-bin/ticket/getticket";

	/**
	 * http GET请求获得jsapi_ticket（有效期7200秒，开发者必须在自己的服务全局缓存jsapi_ticket）
	 *
	 * @param jsApiType jsApi类型
	 * @return JsTicket
	 */
	public static JsTicket getTicket(JsApiType jsApiType) {
		String access_token = AccessTokenApi.getAccessTokenStr();
		String appId = ApiConfigKit.getApiConfig().getAppId();
		String key = appId + ':' + jsApiType.name();
		final ParaMap pm = ParaMap.create("access_token", access_token).put("type", jsApiType.name());

		// 2016.07.21修改By L.cm 为了更加方便扩展
		String jsTicketJson = ApiConfigKit.getAccessTokenCache().get(key);
		JsTicket jsTicket = null;
		if (StrKit.notBlank(jsTicketJson)) {
			jsTicket = new JsTicket(jsTicketJson);
		}
		if (null == jsTicket || !jsTicket.isAvailable()) {
			// 最多三次请求
			jsTicket = RetryUtils.retryOnException(3, new Callable<JsTicket>() {

				@Override
				public JsTicket call() throws Exception {
					return new JsTicket(HttpUtils.get(apiUrl, pm.getData()));
				}

			});

			if (null != jsApiType) {
				ApiConfigKit.getAccessTokenCache().set(key, jsTicket.getCacheJson());
			}

		}
		return jsTicket;
	}

}
