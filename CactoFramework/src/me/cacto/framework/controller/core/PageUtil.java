package me.cacto.framework.controller.core;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import me.cacto.framework.controller.Page;
import me.cacto.framework.controller.annotations.Rewrite;

public class PageUtil {
	public static PageMapping pageMapping(HttpServletRequest request, ControllerConfig config) {
		String uri = request.getRequestURI();

		if (uri == null)
			return null;

		try {
			uri = URLDecoder.decode(uri, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		if (uri.endsWith("/"))
			uri = uri.substring(0, uri.length() - 1);

		if (!uri.startsWith("/"))
			uri = "/" + uri;

		PageMapping pageMapping = new PageMapping();
		Map<String, Class<? extends Page>> pages = config.getPages();

		pageMapping.requestUri = uri;

		if (!config.getServletContext().getContextPath().equals("")) {
			pageMapping.context = config.getServletContext().getContextPath();
			uri = uri.replaceFirst(config.getServletContext().getContextPath(), "");
		} else
			pageMapping.context = "/";

		pageMapping.pageType = pages.get(uri);

		while (uri.contains("/") && pageMapping.pageType == null) {
			uri = uri.substring(0, uri.lastIndexOf("/"));
			pageMapping.pageType = pages.get(uri);
		}

		if (pageMapping.pageType == null)
			return null;

		uri = config.getServletContext().getContextPath() + uri;
		pageMapping.uri = uri;

		for (Rewrite rewrite : pageMapping.pageType.getAnnotationsByType(Rewrite.class)) {
			for (String path : rewrite.value()) {
				if (path.endsWith("/"))
					path = path.substring(0, path.length() - 1);

				if (path.equals(pageMapping.uri))
					pageMapping.language = rewrite.language();
			}
		}

		if (pageMapping.requestUri.length() > pageMapping.uri.length()) {
			String sParameters = pageMapping.requestUri.substring(pageMapping.uri.length() + 1) + " ";

			pageMapping.values = sParameters.split("/");
			if (pageMapping.values.length > 0)
				pageMapping.values[pageMapping.values.length - 1] = pageMapping.values[pageMapping.values.length - 1].trim();
		}

		pageMapping.requestUrl = request.getRequestURL().toString();
		pageMapping.url = request.getRequestURL().toString();

		int p = pageMapping.url.indexOf("/", pageMapping.url.indexOf("//") + 2);

		if (p > 0) {
			pageMapping.url = pageMapping.url.substring(0, p);
			pageMapping.requestUrl = pageMapping.requestUrl.substring(0, p);
		}

		pageMapping.url += config.getServletContext().getContextPath() + pageMapping.uri;
		pageMapping.requestUrl += pageMapping.requestUri;

		return pageMapping;
	}

	//	public static PageMapping pageMapping(HttpServletRequest request, ControllerConfig config) {
	//		String uri = request.getRequestURI();
	//
	//		if (uri == null)
	//			return null;
	//
	//		try {
	//			uri = URLDecoder.decode(uri, "UTF-8");
	//		} catch (UnsupportedEncodingException e) {
	//			e.printStackTrace();
	//		}
	//
	//		if (uri.endsWith("/"))
	//			uri = uri.substring(0, uri.length() - 1);
	//
	//		if (!uri.startsWith("/"))
	//			uri = "/" + uri;
	//
	//		if (config.getMappingExtension() != null) {
	//			if (config.getRequestIgnoreCase() && !uri.toLowerCase().contains("." + config.getMappingExtension().toLowerCase()))
	//				return null;
	//			else if (!uri.contains("." + config.getMappingExtension()))
	//				return null;
	//
	//			uri = uri.replaceFirst("(?i)\\." + config.getMappingExtension(), "");
	//		}
	//
	//		PageMapping pageMapping = new PageMapping();
	//		Map<String, Class<? extends Page>> pages;
	//
	//		if (config.getRequestIgnoreCase()) {
	//			pages = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
	//			pages.putAll(config.getPages());
	//		} else
	//			pages = config.getPages();
	//
	//		pageMapping.requestUri = uri;
	//
	//		if (!config.getServletContext().getContextPath().equals("")) {
	//			pageMapping.context = config.getServletContext().getContextPath();
	//			uri = uri.replaceFirst(config.getServletContext().getContextPath(), "");
	//		} else
	//			pageMapping.context = "/";
	//
	//		pageMapping.pageType = pages.get(uri);
	//
	//		while (uri.contains("/") && pageMapping.pageType == null) {
	//			uri = uri.substring(0, uri.lastIndexOf("/"));
	//			pageMapping.pageType = pages.get(uri);
	//		}
	//
	//		if (pageMapping.pageType == null) {
	//			PageUtil.pageMapping(pageMapping, pages, config);
	//		} else {
	//			if (config.getMappingExtension() != null) {
	//				uri += "." + config.getMappingExtension();
	//			}
	//
	//			uri = config.getServletContext().getContextPath() + uri;
	//
	//			pageMapping.uri = uri;
	//
	//			for (Rewrite rewrite : pageMapping.pageType.getAnnotationsByType(Rewrite.class)) {
	//				for (String path : rewrite.value()) {
	//					if (path.endsWith("/"))
	//						path = path.substring(0, path.length() - 1);
	//
	//					if (config.getRequestIgnoreCase() && path.equalsIgnoreCase(pageMapping.uri))
	//						pageMapping.locale = rewrite.language().getLocale();
	//					else if (path.equals(pageMapping.uri))
	//						pageMapping.locale = rewrite.language().getLocale();
	//				}
	//			}
	//		}
	//
	//		if (pageMapping.pageType == null)
	//			return null;
	//
	//		if (pageMapping.requestUri.length() > pageMapping.uri.length()) {
	//			String sParameters = pageMapping.requestUri.substring(pageMapping.uri.length() + 1) + " ";
	//
	//			pageMapping.values = sParameters.split("[/]");
	//			if (pageMapping.values.length > 0)
	//				pageMapping.values[pageMapping.values.length - 1] = pageMapping.values[pageMapping.values.length - 1].trim();
	//		}
	//
	//		pageMapping.requestUrl = request.getRequestURL().toString();
	//		pageMapping.url = request.getRequestURL().toString();
	//
	//		int p = pageMapping.url.indexOf("/", "https://".length());
	//
	//		if (p > 0) {
	//			pageMapping.url = pageMapping.url.substring(0, p);
	//			pageMapping.requestUrl = pageMapping.requestUrl.substring(0, p);
	//		}
	//
	//		pageMapping.url += config.getServletContext().getContextPath() + pageMapping.uri;
	//		pageMapping.requestUrl += pageMapping.requestUri;
	//
	//		return pageMapping;
	//	}
	//
	//	private static PageMapping pageMapping(PageMapping pageMapping, Map<String, Class<? extends Page>> pages, ControllerConfig config) {
	//		String[] packages = config.getPagePackages();
	//		if (packages == null)
	//			packages = new String[0];
	//
	//		String uri = pageMapping.requestUri;
	//
	//		for (String pkg : packages) {
	//			pageMapping.pageType = pages.get("/" + pkg.replace(".", "/") + uri);
	//			if (pageMapping.pageType != null)
	//				break;
	//		}
	//
	//		while (uri.contains("/") && pageMapping.pageType == null) {
	//			uri = uri.substring(0, uri.lastIndexOf("/"));
	//
	//			for (String pkg : packages) {
	//				pageMapping.pageType = pages.get("/" + pkg.replace(".", "/") + uri);
	//				if (pageMapping.pageType != null)
	//					break;
	//			}
	//		}
	//
	//		if (pageMapping.pageType != null) {
	//			if (config.getMappingExtension() != null) {
	//				uri += "." + config.getMappingExtension();
	//			}
	//
	//			uri = config.getServletContext().getContextPath() + uri;
	//
	//			pageMapping.uri = uri;
	//		}
	//
	//		return pageMapping;
	//	}
	//
	//	//Rewrite
	//	public static String locateUri(Class<? extends Page> pageType, ControllerConfig config) {
	//		return PageUtil.locateUri(pageType, config, null);
	//	}
	//
	//	public static String locateUri(Class<? extends Page> pageType, ControllerConfig config, Language language) {
	//		if (!config.getPages().containsValue(pageType))
	//			return null;
	//
	//		//		if (locale == null)
	//		//			locale = config.getDefaultLocale();
	//
	//		String uri = null;
	//
	//		for (Rewrite rewrite : pageType.getAnnotationsByType(Rewrite.class)) {
	//			if (rewrite.value().length <= 0)
	//				continue;
	//
	//			if (language == null) {
	//				uri = rewrite.value()[0];
	//				break;
	//			} else if (rewrite.language().getLocale() == null) {
	//				uri = rewrite.value()[0];
	//				break;
	//			} else if (language.getLocale().toLanguageTag().equals(rewrite.language().getLocale().toLanguageTag())) {
	//				uri = rewrite.value()[0];
	//				break;
	//			}
	//		}
	//
	//		if (uri == null) {
	//			uri = pageType.getCanonicalName().replace(".", "/");
	//
	//			String[] packages = config.getPagePackages();
	//			if (packages == null)
	//				packages = new String[0];
	//
	//			for (String pkg : packages) {
	//				uri = uri.replaceFirst(pkg.replace(".", "/"), "");
	//
	//				if (!uri.equals(pageType.getCanonicalName().replace(".", "/")))
	//					break;
	//			}
	//		}
	//
	//		if (config.getMappingExtension() != null)
	//			uri += "." + config.getMappingExtension();
	//
	//		if (!uri.startsWith("/"))
	//			uri = "/" + uri;
	//
	//		uri = config.getServletContext().getContextPath() + uri;
	//		return uri;
	//	}
}
