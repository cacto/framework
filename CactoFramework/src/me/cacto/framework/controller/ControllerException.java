package me.cacto.framework.controller;

import javax.servlet.ServletException;

public class ControllerException extends ServletException {
	private static final long serialVersionUID = 1L;

	public ControllerException() {
		super();
	}

	public ControllerException(Throwable throwable) {
		super(throwable);
	}

	public ControllerException(String message) {
		super(message);
	}

	public ControllerException(String message, Throwable throwable) {
		super(message, throwable);
	}
}
