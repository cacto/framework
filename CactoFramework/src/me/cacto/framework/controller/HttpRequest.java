package me.cacto.framework.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Vector;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import me.cacto.framework.common.Constants;
import me.cacto.framework.controller.annotations.MappingParameters;
import me.cacto.framework.controller.core.ControllerConfig;
import me.cacto.framework.controller.core.PageMapping;

public class HttpRequest extends HttpServletRequestWrapper {
	private final PageMapping pageMapping;
	private final HttpServletRequest httpServletRequest;
	private Map<String, String[]> parameterMap;
	private String[] mappingParameters = new String[0];

	public HttpRequest(HttpServletRequest httpServletRequest, PageMapping pageMapping) {
		super(httpServletRequest);
		this.httpServletRequest = httpServletRequest;
		this.pageMapping = pageMapping;

		if (pageMapping != null) {
			MappingParameters parameters = this.pageMapping.getPageType().getAnnotation(MappingParameters.class);
			if (parameters != null)
				this.mappingParameters = parameters.value();
		}
	}

	public HttpServletRequest getHttpServletRequest() {
		return this.httpServletRequest;
	}

	public void setMappingParameters(String... parameters) {
		if (parameters == null)
			parameters = new String[0];

		this.parameterMap = null;
		this.mappingParameters = parameters;
	}

	public String[] getMappingParameters() {
		return this.mappingParameters;
	}

	@Override
	public String getRequestURI() {
		return this.pageMapping.getUri();
	}

	@Override
	public StringBuffer getRequestURL() {
		return new StringBuffer(this.pageMapping.getRequestUrl());
	}

	@SuppressWarnings("unchecked")
	public <T> T getBean(String name) {
		return (T) this.getAttribute(name);
	}

	@SuppressWarnings("unchecked")
	public <T> T getBean(Class<? extends T> type) {
		for (String name : Collections.list(this.getAttributeNames())) {
			Object object = this.getAttribute(name);
			if (object != null && type.isInstance(object))
				return (T) object;
		}

		return null;
	}

	private ControllerConfig getControllerConfig() {
		return ControllerConfig.getInstance(this.getServletContext());
	}

	@Override
	public Locale getLocale() {
		Locale locale = (Locale) this.getAttribute(Constants.LOCALE);

		if (this.getControllerConfig().i18n().existLocale(locale))
			return locale;

		locale = (Locale) this.getSession().getAttribute(Constants.LOCALE);

		if (this.getControllerConfig().i18n().existLocale(locale))
			return locale;

		locale = this.getControllerConfig().getDefaultLanguage().getLocale();

		if (this.getControllerConfig().i18n().existLocale(locale))
			return locale;

		locale = super.getLocale();

		if (this.getControllerConfig().i18n().existLocale(locale))
			return locale;

		return Locale.getDefault();
	}

	@Override
	public String getParameter(String name) {
		String[] values = this.getParameterMap().get(name);
		if (values == null || values.length == 0)
			return null;

		return values[0];
	}

	@Override
	public String[] getParameterValues(String name) {
		String[] values = this.getParameterMap().get(name);
		if (values == null)
			return null;

		return values;
	}

	@Override
	public Enumeration<String> getParameterNames() {
		return new Vector<String>(this.getParameterMap().keySet()).elements();
	}

	@Override
	public Map<String, String[]> getParameterMap() {
		if (this.parameterMap == null) {
			this.parameterMap = new HashMap<String, String[]>();
			String parameters[] = this.getMappingParameters();

			if (this.pageMapping != null) {
				for (int i = 0; i < parameters.length && i < this.pageMapping.getValues().length; i++) {
					String name = parameters[i];
					String value = this.pageMapping.getValues()[i];

					if (value != null && value.equals(""))
						continue;

					String[] values = this.parameterMap.get(name);
					List<String> list;

					if (values == null) {
						list = new ArrayList<String>();
					} else
						list = Arrays.asList(values);

					list.add(value);
					this.parameterMap.put(name, list.toArray(new String[0]));
				}
			}

			for (String key : Collections.list(super.getParameterNames())) {
				this.parameterMap.put(key, super.getParameterValues(key));
			}
		}

		return this.parameterMap;
	}
}
