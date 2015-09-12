package me.cacto.framework.controller.events;

import me.cacto.framework.controller.ControllerException;

public interface OnException {
	public default void onException(Throwable throwable) throws ControllerException {
		throw new ControllerException(throwable);
	}
}
