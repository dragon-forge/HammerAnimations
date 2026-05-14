package dev.zeith.lzvm.jvm;

import java.lang.annotation.*;

@Target({ })
@Retention(RetentionPolicy.RUNTIME)
public @interface Generated
{
	String value();
	
	int argCount() default 0;
}