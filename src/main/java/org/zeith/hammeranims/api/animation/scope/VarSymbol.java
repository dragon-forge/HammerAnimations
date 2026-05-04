package org.zeith.hammeranims.api.animation.scope;

import lombok.Value;

@Value
public class VarSymbol<T>
{
	String name;
	
	public static <T> VarSymbol<T> of(String name)
	{
		return new VarSymbol<>(name);
	}
}