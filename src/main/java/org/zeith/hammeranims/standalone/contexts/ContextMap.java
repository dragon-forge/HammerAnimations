package org.zeith.hammeranims.standalone.contexts;

import org.zeith.hammeranims.standalone.utils.Cast;

import java.util.HashMap;
import java.util.Map;

public class ContextMap
{
	private final Map<ContextToken<?>, Object> storage = new HashMap<>();
	
	public <T> T put(ContextToken<T> token, T data)
	{
		return Cast.cast(storage.put(token, data));
	}
	
	public <T> T get(ContextToken<T> token)
	{
		if(token.defaultValue() != null)
			return Cast.cast(storage.computeIfAbsent(token, t -> t.defaultValue().get()));
		return Cast.cast(storage.get(token));
	}
}