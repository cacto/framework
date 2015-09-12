package me.cacto.framework.controller;

import me.cacto.util.i18n.Language;

public class PageMapping {
	String requestUri;
	String requestUrl;
	String uri;
	String url;
	String context;
	String[] values = new String[0];
	Language language;
	Class<? extends Page> pageType;

	public String getRequestUri() {
		return this.requestUri;
	}
	public String getRequestUrl() {
		return this.requestUrl;
	}
	public String getUri() {
		return this.uri;
	}
	public String getUrl() {
		return this.url;
	}
	public String getContext() {
		return this.context;
	}
	public String[] getValues() {
		return this.values;
	}
	public Language getLanguage() {
		return language;
	}
	public Class<? extends Page> getPageType() {
		return this.pageType;
	}
}
