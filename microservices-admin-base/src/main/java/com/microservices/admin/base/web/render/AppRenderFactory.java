package com.microservices.admin.base.web.render;

import com.jfinal.render.ErrorRender;
import com.jfinal.render.Render;
import com.microservices.web.render.MicroservicesRenderFactory;

/**
 * RenderFactory，覆盖microservices error render
 */
public class AppRenderFactory extends MicroservicesRenderFactory {

	@Override
	public Render getRender(String view) {
		return super.getRender(view);
	}

	@Override
	public Render getErrorRender(int errorCode) {
		return new ErrorRender(errorCode, constants.getErrorView(errorCode));
	}

}
