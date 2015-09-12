package me.cacto.framework.controller.core;

import java.util.EnumSet;
import javax.servlet.DispatcherType;
import javax.servlet.FilterRegistration;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.ServletRegistration.Dynamic;
import me.cacto.framework.common.Constants;
import me.cacto.framework.controller.core.ControllerConfig;

public abstract class ControllerDefaultListener implements ServletContextListener {
	public abstract void initialized(ControllerConfig config);
	public abstract void destroyed(ControllerConfig config);

	@Override
	public void contextDestroyed(ServletContextEvent contextEvent) {
		this.destroyed(ControllerConfig.getInstance(contextEvent.getServletContext()));
	}

	@Override
	public final void contextInitialized(ServletContextEvent contextEvent) {
		ControllerConfig contextConfig = new ControllerConfig(contextEvent.getServletContext());
		this.initialized(contextConfig);

		Dynamic defaultServlet = contextEvent.getServletContext().addServlet(ControllerDefaultServlet.class.getName(), ControllerDefaultServlet.class);
		defaultServlet.setLoadOnStartup(-1);
		defaultServlet.addMapping(Constants.DEFAULT_SERVLET);

		FilterRegistration defaultFilter = contextEvent.getServletContext().addFilter(ControllerDefaultFilter.class.getCanonicalName(), ControllerDefaultFilter.class);
		defaultFilter.addMappingForUrlPatterns(EnumSet.of(DispatcherType.REQUEST, DispatcherType.ASYNC), true, "/*");
	}
}
