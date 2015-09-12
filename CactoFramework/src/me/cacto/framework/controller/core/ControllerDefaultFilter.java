package me.cacto.framework.controller.core;

import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import me.cacto.framework.common.Constants;
import me.cacto.framework.controller.HttpController;
import me.cacto.framework.controller.HttpRequest;
import me.cacto.framework.controller.HttpResponse;
import me.cacto.framework.controller.Page;
import me.cacto.framework.controller.PageMapping;
import me.cacto.framework.controller.PageUtil;
import me.cacto.util.lang.ClassUtil;
import me.cacto.util.lang.FieldUtil;

public class ControllerDefaultFilter implements Filter {
	@Override
	public void destroy() {
	}

	@Override
	public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
		ServletContext context = servletRequest.getServletContext();
		ControllerConfig config = ControllerConfig.getInstance(context);

		HttpServletRequest request = (HttpServletRequest) servletRequest;
		HttpServletResponse response = (HttpServletResponse) servletResponse;

		PageMapping pageMapping = PageUtil.pageMapping(request, config);

		if (pageMapping == null) {
			filterChain.doFilter(servletRequest, servletResponse);
			return;
		}

		Page page = ClassUtil.newInstance(pageMapping.getPageType());

		//		if (pageMapping.getPageType().isAnnotationPresent(RequireAuthentication.class)) {
		//			RequireAuthentication requireAuthentication = pageMapping.getPageType().getAnnotation(RequireAuthentication.class);
		//
		//			if (requireAuthentication.required()) {
		//				Class<? extends IsAuthenticated> isAuthenticationType = requireAuthentication.checkIsAuthenticated();
		//				if (isAuthenticationType.equals(IsAuthenticated.class))
		//					isAuthenticationType = config.getDefaultIsAuthenticated();
		//
		//				if (isAuthenticationType != null) {
		//					IsAuthenticated isAuthentication = ClassUtil.newInstance(isAuthenticationType);
		//					Boolean auth = isAuthentication.isAuthenticated(pageMapping.getPageType(), request);
		//
		//					if (auth == null || !auth) {
		//						Class<? extends Page> authenticationPageType = requireAuthentication.authenticationPage();
		//						if (authenticationPageType.equals(Page.class))
		//							authenticationPageType = config.getDefaultPageAuthentication();
		//
		//						if (authenticationPageType == null || authenticationPageType.equals(pageMapping.getPageType())) {
		//							response.sendError(401);
		//							return;
		//						}
		//
		//						String uri = PageUtil.locateUri(authenticationPageType, config);
		//
		//						if (uri == null)
		//							response.sendError(401);
		//						else
		//							response.sendRedirect(uri);
		//
		//						return;
		//
		//						//FieldUtil.set(PageMapping.class, "pageType", authenticationPageType, request);
		//						//page = ClassUtil.newInstance(pageMapping.getPageType());
		//					}
		//				}
		//			}
		//		}

		HttpController controller = new HttpController();
		HttpRequest requestWrapper = new HttpRequest(request, pageMapping);
		HttpResponse responseWrapper = new HttpResponse(response, request, controller);

		FieldUtil.set(HttpController.class, "request", controller, requestWrapper);
		FieldUtil.set(HttpController.class, "response", controller, responseWrapper);
		FieldUtil.set(HttpController.class, "pageMapping", controller, pageMapping);
		FieldUtil.set(HttpController.class, "page", controller, page);

		FieldUtil.set(Page.class, "controller", page, controller);
		FieldUtil.set(Page.class, "request", page, requestWrapper);
		FieldUtil.set(Page.class, "response", page, responseWrapper);
		FieldUtil.set(Page.class, "session", page, requestWrapper.getSession());

		request.setAttribute(Constants.CONTROLLER_ATTRIBUTE, controller);
		request.getRequestDispatcher(Constants.DEFAULT_SERVLET).forward(requestWrapper, responseWrapper);
	}


	@Override
	public void init(FilterConfig filterConfig) throws ServletException {

	}
}
