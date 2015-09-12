package me.cacto.framework.controller.core;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import me.cacto.framework.common.Constants;
import me.cacto.framework.common.HttpMethod;
import me.cacto.framework.controller.ControllerException;
import me.cacto.framework.controller.HttpController;
import me.cacto.framework.controller.HttpRequest;
import me.cacto.framework.controller.HttpResponse;
import me.cacto.framework.controller.Page;
import me.cacto.framework.controller.annotations.Attribute;
import me.cacto.framework.controller.annotations.Bean;
import me.cacto.framework.controller.annotations.Event;
import me.cacto.framework.controller.annotations.Jsp;
import me.cacto.framework.controller.annotations.Pattern;
import me.cacto.framework.controller.annotations.Template;
import me.cacto.framework.controller.events.DoGet;
import me.cacto.framework.controller.events.DoPost;
import me.cacto.framework.controller.events.OnDestroy;
import me.cacto.framework.controller.events.OnEnd;
import me.cacto.framework.controller.events.OnException;
import me.cacto.framework.controller.events.OnInitialize;
import me.cacto.framework.controller.events.OnStart;
import me.cacto.util.format.ToObject;
import me.cacto.util.lang.ClassUtil;
import me.cacto.util.lang.FieldUtil;
import me.cacto.util.lang.MethodUtil;

class ControllerDefaultProcess {
	private final HttpRequest request;
	private final HttpResponse response;
	private final HttpMethod httpMethod;
	private final HttpController controller;

	ControllerDefaultProcess(HttpServletRequest request, HttpServletResponse response, HttpServlet servlet, HttpMethod httpMethod) throws ServletException, IOException {
		this.controller = (HttpController) request.getAttribute(Constants.CONTROLLER_ATTRIBUTE);

		if (this.controller == null)
			throw new ControllerException("Controller not set");

		this.request = (HttpRequest) request;
		this.response = (HttpResponse) response;
		this.httpMethod = httpMethod;
	}

	void execute() throws ServletException {
		Page page = this.controller.getPage();

		try {
			this.loadBeans(page);

			if (page instanceof OnInitialize) {
				((OnInitialize) page).onInitialize();
			}

			if (!this.controller.isAbort()) {
				if (page instanceof OnStart) {
					((OnStart) page).onStart();
				}

				if (!this.controller.isAbort()) {
					if (page instanceof DoGet && this.httpMethod == HttpMethod.GET) {
						((DoGet) page).doGet();
					} else if (page instanceof DoPost && this.httpMethod == HttpMethod.POST) {
						((DoPost) page).doPost();
					}

					if (!this.controller.isAbort()) {
						this.invokeMethod(page);

						if (!this.controller.isAbort()) {
							this.setRequestAttributes(page);

							this.request.setAttribute("page", page);
							this.request.setAttribute("controller", this.controller);
							this.request.setAttribute("request", this.request);
							this.request.setAttribute("httpMethod", this.httpMethod.name());

							if (!this.controller.isAbort()) {
								Template template = this.controller.getTemplate();
								Jsp jsp = this.controller.getJsp();

								if (template != null) {
									if (jsp != null)
										this.request.setAttribute(Constants.TEMPLATE_BODY, jsp.value());

									this.request.getRequestDispatcher(template.value()).include(this.request, this.response);
								} else if (jsp != null) {
									this.request.getRequestDispatcher(jsp.value()).include(this.request, this.response);
								}

								if (page instanceof OnEnd) {
									((OnEnd) page).onEnd();
								}
							}
						}
					}
				}
			}
		} catch (Throwable throwable) {
			if (page instanceof OnException) {
				((OnException) page).onException(throwable);
			} else if (throwable instanceof ControllerException)
				throw (ControllerException) throwable;
			else
				throw new ControllerException(throwable);
		} finally {
			if (page instanceof OnDestroy) {
				((OnDestroy) page).onDestroy();
			}
		}
	}

	private void loadBeans(Page page) throws ControllerException {
		for (Bean bean : page.getClass().getAnnotationsByType(Bean.class)) {
			String name = bean.name();
			Object object = this.request.getAttribute(name);

			if (object == null)
				object = ClassUtil.newInstance(bean.type());

			List<Field> fields = FieldUtil.listFields(bean.type());
			Boolean found = false;

			for (Field field : fields) {
				String value = this.request.getParameter(name + "." + field.getName());

				if (value == null)
					continue;

				String pattern = null;
				if (field.isAnnotationPresent(Pattern.class))
					pattern = field.getAnnotation(Pattern.class).value();

				try {
					FieldUtil.set(field, object, ToObject.valueOf(field.getType(), value, pattern));
					found = true;
				} catch (Exception ex) {
					String msg = String.format("%s: %s %s.%s = %s", ex.getClass().getSimpleName(), field.getType().getName(), field.getDeclaringClass().getName(), field.getName(), ex.getMessage());
					throw new ControllerException(msg);
				}
			}

			if (found)
				this.request.setAttribute(name, object);
		}
	}

	private void setRequestAttributes(Page page) {
		for (Method method : MethodUtil.listMethods(page.getClass(), Attribute.class)) {
			Attribute attribute = method.getAnnotation(Attribute.class);
			if (attribute.httpMethod() != HttpMethod.UNKNOWN && attribute.httpMethod() == this.httpMethod)
				continue;

			Object value = MethodUtil.invoke(method, page);

			if (this.controller.isAbort())
				break;

			if (value == null)
				this.request.removeAttribute(attribute.name());
			else
				this.request.setAttribute(attribute.name(), value);
		}
	}

	private void invokeMethod(Page page) {
		for (Method method : MethodUtil.listMethods(page.getClass(), Event.class)) {
			Event event = method.getAnnotation(Event.class);
			if (event.httpMethod() != HttpMethod.UNKNOWN && event.httpMethod() == this.httpMethod)
				continue;

			String name = event.name();
			if (name.equals(""))
				name = method.getName();

			if (this.request.getParameter(name + "()") == null)
				continue;

			MethodUtil.invoke(method, page);

			if (this.controller.isAbort())
				break;
		}
	}
}
