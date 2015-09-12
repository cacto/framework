package me.cacto.framework.controller;

import java.lang.annotation.Annotation;
import java.util.Date;
import javax.servlet.http.HttpSession;
import me.cacto.framework.controller.annotations.Jsp;
import me.cacto.framework.controller.annotations.Template;
import me.cacto.framework.controller.core.ControllerConfig;
import me.cacto.framework.controller.core.PageMapping;

public class HttpController {
	private final Page page = null;
	private final PageMapping pageMapping = null;
	private final HttpRequest request = null;
	private final HttpResponse response = null;
	private final Long startTime = new Date().getTime();

	private String jsp = "";
	private String template = "";

	private Boolean abort = Boolean.FALSE;

	public Long getPageLoadTime() {
		return new Date().getTime() - this.startTime;
	}

	public Page getPage() {
		return this.page;
	}

	public Class<? extends Page> getPageType() {
		return this.pageMapping.getPageType();
	}

	public ControllerConfig getControllerConfig() {
		return ControllerConfig.getInstance(this.request.getServletContext());
	}

	public HttpRequest getHttpRequest() {
		return this.request;
	}

	public HttpResponse getHttpResponse() {
		return this.response;
	}

	public HttpSession getHttpSession() {
		return this.request.getSession();
	}

	public Jsp getJsp() {
		if (this.jsp == null)
			return null;
		else if (!this.jsp.equals("")) {
			return new Jsp() {
				@Override
				public Class<? extends Annotation> annotationType() {
					return Jsp.class;
				}

				@Override
				public String value() {
					return HttpController.this.jsp;
				}
			};
		} else
			return this.getPageType().getAnnotation(Jsp.class);
	}

	public void setJsp(String jsp) {
		this.jsp = jsp;
	}

	public void resetJsp() {
		this.jsp = "";
	}

	public Template getTemplate() {
		if (this.template == null)
			return null;
		else if (!this.template.equals("")) {
			return new Template() {
				@Override
				public Class<? extends Annotation> annotationType() {
					return Template.class;
				}

				@Override
				public String value() {
					return HttpController.this.template;
				}
			};
		} else
			return this.getPageType().getAnnotation(Template.class);
	}

	public void setTemplate(String template) {
		this.template = template;
	}

	public void resetTemplate() {
		this.template = "";
	}

	//Custom
	//	public Locale getLocale() {
	//		return this.request.getLocale();
	//	}
	//
	//	public void setLocale(Locale locale) {
	//		this.response.setLocale(locale);
	//	}
	//
	//	public void setSessionLocale(Locale locale) {
	//		this.session.setAttribute(Constants.LOCALE, locale);
	//	}
	//
	//	public Locale getSessionLocale(Locale locale) {
	//		return (Locale) this.session.getAttribute(Constants.LOCALE);
	//	}

	public void abort() {
		this.abort = Boolean.TRUE;
	}

	public Boolean isAbort() {
		return this.abort;
	}

	public void cancelAbort() {
		this.abort = Boolean.FALSE;
	}
}
