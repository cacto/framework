package me.cacto.framework.controller.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import me.cacto.framework.controller.Page;
import me.cacto.framework.controller.events.IsAuthenticated;

@Inherited
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface RequireAuthentication {
	Class<? extends Page> authenticationPage() default Page.class;
	Class<? extends IsAuthenticated> checkIsAuthenticated() default IsAuthenticated.class;
	boolean required() default true;
}
