package me.cacto.framework.controller.events;

import javax.servlet.http.HttpServletRequest;
import me.cacto.framework.controller.Page;

public interface IsAuthenticated {
	public Boolean isAuthenticated(Class<? extends Page> type, HttpServletRequest servletRequest);
}
