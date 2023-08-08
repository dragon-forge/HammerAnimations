package org.zeith.hammer.models.api.annotations;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
public @interface AnimKey
{
	String value();
}