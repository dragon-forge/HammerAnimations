package org.zeith.hammeranims.molang;

import org.zeith.hammeranims.molang.runtime.MoLangEnvironment;
import org.zeith.hammeranims.molang.runtime.MoScope;
import org.zeith.hammeranims.molang.runtime.value.MoValue;

import java.util.HashMap;
import java.util.Map;

public interface Expression
{
	Map<String, Object> attributes = new HashMap<>();
	
	default Map<String, Object> getAttributes()
	{
		return attributes;
	}
	
	MoValue evaluate(MoScope scope, MoLangEnvironment environment);
	
	default void assign(MoScope scope, MoLangEnvironment environment, MoValue value)
	{
		throw new RuntimeException("Cannot assign a value to " + this.getClass());
	}
}