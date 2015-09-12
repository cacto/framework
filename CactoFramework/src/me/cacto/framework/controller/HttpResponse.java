package me.cacto.framework.controller;

import java.io.IOException;
import java.util.Locale;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import me.cacto.framework.common.Constants;
import me.cacto.framework.controller.core.PageUtil;

public class HttpResponse extends HttpServletResponseWrapper {
	private String location;
	private final HttpServletRequest request;
	private final HttpController controller;

	public HttpResponse(HttpServletResponse response, HttpServletRequest request, HttpController controller) {
		super(response);
		this.request = request;
		this.controller = controller;
	}

	@Override
	public void setLocale(Locale locale) {
		this.request.setAttribute(Constants.LOCALE, locale);
		super.setLocale(locale);
	}

	public String getLocation() {
		return this.location;
	}

	@Override
	public void sendRedirect(String location) throws IOException {
		this.location = location;

		if (this.location != null)
			this.controller.abort();
		else
			this.controller.cancelAbort();
	}

	public void sendRedirect(Class<? extends Page> pageType) {
		this.location = PageUtil.locateUri(pageType, this.controller.getControllerConfig(), this.request.getLocale());

		if (this.location != null)
			this.controller.abort();
		else
			this.controller.cancelAbort();
	}

	public void cancelRedirect() {
		this.location = null;
		this.controller.cancelAbort();
	}
}
