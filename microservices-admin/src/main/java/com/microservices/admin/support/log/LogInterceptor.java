package com.microservices.admin.support.log;

import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;
import eu.bitwalker.useragentutils.Browser;
import eu.bitwalker.useragentutils.UserAgent;
import com.microservices.Microservices;
import com.microservices.admin.base.web.base.BaseController;
import com.microservices.admin.service.api.LogService;
import com.microservices.admin.service.entity.model.Log;
import com.microservices.admin.service.entity.model.User;
import com.microservices.admin.support.auth.AuthUtils;

import java.util.Date;

/**
 * 系统日志拦截器
 */
public class LogInterceptor implements Interceptor {

	private final static com.jfinal.log.Log logger = com.jfinal.log.Log.getLog(LogInterceptor.class);

	@Override
	public void intercept(Invocation inv) {
		if (inv.getController() instanceof BaseController) {
			BaseController c = (BaseController) inv.getController();

			User user = AuthUtils.getLoginUser();
			UserAgent userAgent = UserAgent.parseUserAgentString(c.getRequest().getHeader("User-Agent"));
			Browser browser = userAgent.getBrowser();

			Log log = new Log();
			log.setUid(user.getId());
			log.setBrowser(browser.getName());
			log.setOperation(c.getRequest().getMethod());
			log.setFrom(c.getReferer());
			log.setIp(c.getIPAddress());
			log.setUrl(c.getRequest().getRequestURI());
			log.setCreateDate(new Date());
			log.setLastUpdAcct(user.getId() == null ? "guest" : user.getName());
			log.setLastUpdTime(new Date());
			log.setNote("记录日志");

			try {
				LogService logService = Microservices.service(LogService.class);
				logService.save(log);
			} catch (Exception e) {
				logger.error(e.getMessage());
				e.printStackTrace();
			} finally {
				inv.invoke();
			}
		} else {
			inv.invoke();
		}
	}

}
