package com.microservices.web.render;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import com.jfinal.render.Render;
import com.jfinal.render.RenderException;
import com.jfinal.render.RenderManager;
import com.microservices.exception.MicroservicesExceptionHolder;

/**
 * MicroservicesErrorRender.
 */
public class MicroservicesErrorRender extends Render {

	protected static final String contentType = "text/html; charset=" + getEncoding();

	protected static final String poweredBy = "<center><a href='http://microservices.io' target='_blank'><b>Powered by Microservices</b></a></center>";

	protected static final String html404 = "<html><head><title>404 Not Found</title></head><body bgcolor='white'><center><h1>404 Not Found</h1></center><hr>" + poweredBy + "</body></html>";
	protected static final String html401 = "<html><head><title>401 Unauthorized</title></head><body bgcolor='white'><center><h1>401 Unauthorized</h1></center><hr>" + poweredBy + "</body></html>";
	protected static final String html403 = "<html><head><title>403 Forbidden</title></head><body bgcolor='white'><center><h1>403 Forbidden</h1></center><hr>" + poweredBy + "</body></html>";

	protected static final String html500 = "<html><head><title>500 Internal Server Error</title></head>" + "<body bgcolor='white'><center><h1>500 Internal Server Error</h1></center>" + "<hr>" + "%s" + "<hr>" + poweredBy + "</body></html>";

	protected int errorCode;

	public MicroservicesErrorRender(int errorCode, String view) {
		this.errorCode = errorCode;
		this.view = view;
	}

	@Override
	public void render() {
		response.setStatus(getErrorCode());

		// render with view
		String view = getView();
		if (view != null) {
			RenderManager.me().getRenderFactory().getRender(view).setContext(request, response).render();
			return;
		}

		try {
			response.setContentType(contentType);
			PrintWriter writer = response.getWriter();
			writer.write(getErrorHtml());
		} catch (IOException e) {
			throw new RenderException(e);
		}
	}

	public String getErrorHtml() {
		int errorCode = getErrorCode();
		if (errorCode == 404)
			return html404;
		if (errorCode == 401)
			return html401;
		if (errorCode == 403)
			return html403;
		if (errorCode == 500)
			return build500ErrorInfo();
		return "<html><head><title>" + errorCode + " Error</title></head><body bgcolor='white'><center><h1>" + errorCode + " Error</h1></center><hr>" + poweredBy + "</body></html>";
	}

	public int getErrorCode() {
		return errorCode;
	}

	public String build500ErrorInfo() {
		List<Throwable> throwables = MicroservicesExceptionHolder.throwables();
		if (throwables == null || throwables.size() == 0) {
			return String.format(html500, "");
		}

		StringBuilder stringBuilder = new StringBuilder();
		for (Throwable throwable : throwables) {
			stringBuilder.append(throwable.getClass().getName() + " : " + throwable.getMessage() + "<br />");
			StackTraceElement[] elems = throwable.getStackTrace();
			for (StackTraceElement element : elems) {
				stringBuilder.append("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;at " + element + "<br />");
			}
		}

		return String.format(html500, stringBuilder.toString());
	}
}
