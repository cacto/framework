package me.cacto.framework.controller.core;

import java.util.HashMap;
import java.util.Map;
import javax.servlet.ServletContext;
import me.cacto.framework.controller.Page;
import me.cacto.framework.controller.annotations.Rewrite;
import me.cacto.framework.controller.events.IsAuthenticated;
import me.cacto.util.i18n.I18n;
import me.cacto.util.i18n.Language;

public class ControllerConfig {
	private final Map<String, Class<? extends Page>> pagesMap = new HashMap<>();
	private Language defaultLanguage;
	//private String mappingExtension;
	//private String[] pagePackages;
	public final I18n i18n = new I18n();

	private Class<? extends IsAuthenticated> defaultIsAuthenticated;
	private Class<? extends Page> defaultPageAuthentication;

	private final ServletContext servletContext;
	//private Boolean requestIgnoreCase = Boolean.FALSE;

	ControllerConfig(ServletContext servletContext) {
		this.servletContext = servletContext;
		servletContext.setAttribute(ControllerConfig.class.getCanonicalName(), this);
	}

	public static ControllerConfig getInstance(ServletContext context) {
		return (ControllerConfig) context.getAttribute(ControllerConfig.class.getCanonicalName());
	}

	public final ServletContext getServletContext() {
		return this.servletContext;
	}

	//Custom

	// Language
	public I18n i18n() {
		return i18n;
	}

	public final Language getDefaultLanguage() {
		return this.defaultLanguage;
	}

	public final void setDefaultLanguage(Language defaultLanguage) {
		this.defaultLanguage = defaultLanguage;
	}

	//
	public void registerPage(Class<? extends Page> pageType) {
		for (Rewrite mapping : pageType.getAnnotationsByType(Rewrite.class)) {
			for (String path : mapping.value()) {
				if (path.endsWith("/"))
					path = path.substring(0, path.length() - 1);

				this.pagesMap.put(path, pageType);
			}
		}

		this.pagesMap.put("/" + pageType.getCanonicalName().replace(".", "/"), pageType);
	}

	public void unregisterPage(Class<? extends Page> pageType) {
		for (Rewrite mapping : pageType.getAnnotationsByType(Rewrite.class)) {
			for (String path : mapping.value()) {
				if (path.endsWith("/"))
					path = path.substring(0, path.length() - 1);

				this.pagesMap.remove(path);
			}
		}

		this.pagesMap.remove("/" + pageType.getCanonicalName().toLowerCase().replace(".", "/"));
	}

	public Map<String, Class<? extends Page>> getPages() {
		return this.pagesMap;
	}

	// Authentication
	public void setDefaultPageAuthentication(Class<? extends Page> defaultPageAuthentication) {
		this.defaultPageAuthentication = defaultPageAuthentication;
	}

	public Class<? extends Page> getDefaultPageAuthentication() {
		return this.defaultPageAuthentication;
	}

	public void setDefaultIsAuthenticated(Class<? extends IsAuthenticated> defaultIsAuthenticated) {
		this.defaultIsAuthenticated = defaultIsAuthenticated;
	}

	public Class<? extends IsAuthenticated> getDefaultIsAuthenticated() {
		return this.defaultIsAuthenticated;
	}

	//	public final void setRequestIgnoreCase(Boolean requestIgnoreCase) {
	//		if (requestIgnoreCase == null)
	//			requestIgnoreCase = Boolean.FALSE;
	//
	//		this.requestIgnoreCase = requestIgnoreCase;
	//	}
	//
	//	public final Boolean getRequestIgnoreCase() {
	//		return this.requestIgnoreCase;
	//	}
	//
	//	public String[] getPagePackages() {
	//		return this.pagePackages;
	//	}
	//
	//	public void setPagePackages(String... pagePackages) {
	//		this.pagePackages = pagePackages;
	//	}
	//
	//	public void setMappingExtension(String mappingExtension) {
	//		if (mappingExtension != null) {
	//			while (mappingExtension.startsWith("*"))
	//				mappingExtension = mappingExtension.substring(1);
	//
	//			while (mappingExtension.startsWith("."))
	//				mappingExtension = mappingExtension.substring(1);
	//		}
	//
	//		this.mappingExtension = mappingExtension;
	//	}
	//
	//	public String getMappingExtension() {
	//		return this.mappingExtension;
	//	}
}