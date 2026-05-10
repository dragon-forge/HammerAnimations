package org.zeith.hammeranims.api.annotation;

import org.zeith.hammeranims.core.contents.actions.MethodAnimAction;

import java.lang.annotation.*;

/**
 * This annotation flags method as callable for {@link MethodAnimAction}.
 * <p>
 * This is required to prevent potential security issues when deserializing an instance of method handle.
 * <p>
 * While niche, it can be a security nightmare, thus this annotation is in place.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ExposedToAnimAction
{
}