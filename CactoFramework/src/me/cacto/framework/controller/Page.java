package me.cacto.framework.controller;

import javax.servlet.http.HttpSession;

public abstract class Page {
	protected final HttpController controller = null;
	protected final HttpRequest request = null;
	protected final HttpResponse response = null;
	protected final HttpSession session = null;

	public String getPageName() {
		return this.getClass().getSimpleName();
	}
}
