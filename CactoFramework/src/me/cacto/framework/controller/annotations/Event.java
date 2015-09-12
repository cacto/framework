package me.cacto.framework.controller.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import me.cacto.framework.common.HttpMethod;

@Inherited
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Event {
	String name() default "";
	HttpMethod httpMethod() default HttpMethod.UNKNOWN;
}
