package org.zeith.hammeranims.api.animation.scope;

import java.util.*;
import java.util.function.Supplier;

@SuppressWarnings("unchecked")
public class Variables
{
	protected final Map<String, Object> storage;
	
	public Variables(Map<String, Object> storage)
	{
		this.storage = storage;
	}
	
	public Variables()
	{
		this(new HashMap<>());
	}
	
	public <T> T get(VarSymbol<T> sym)
	{
		return (T) storage.get(sym.getName());
	}
	
	public <T> T put(VarSymbol<T> sym, T val)
	{
		return (T) storage.put(sym.getName(), val);
	}
	
	public <T> T computeIfAbsent(VarSymbol<T> sym, Supplier<T> val)
	{
		return (T) storage.computeIfAbsent(sym.getName(), (f) -> val.get());
	}
}