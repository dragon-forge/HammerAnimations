package org.zeith.hammeranims.molang.runtime.struct;

import org.zeith.hammeranims.molang.runtime.*;
import org.zeith.hammeranims.molang.runtime.value.*;

import java.util.*;
import java.util.function.Function;

public record QueryStruct(MoLangEnvironment environment, Map<String, Function<MoParams, Object>> functions)
		implements MoStruct
{
	public QueryStruct setFunction(String name, Function<MoParams, Object> func)
	{
		this.functions.put(name, func);
		return this;
	}
	
	public QueryStruct setValue(String name, Object value)
	{
		MoValue mo = MoValue.of(value);
		this.functions.put(name, p -> mo);
		return this;
	}
	
	@Override
	public MoValue get(Iterator<String> names, MoParams params)
	{
		String key = names.next();
		Function<MoParams, Object> func = this.functions.get(key);
		MoParams currentParams = names.hasNext() ? environment.emptyParams : params;
		if(func != null)
		{
			Object result = func.apply(currentParams);
			return result instanceof MoStruct && names.hasNext() ? ((MoStruct) result).get(names, params) : MoValue.of(result);
		} else
		{
			return DoubleValue.ZERO;
		}
	}
	
	@Override
	public MoLangEnvironment getEnvironment()
	{
		return environment;
	}
	
	@Override
	public void set(Iterator<String> names, MoValue value)
	{
		String main = names.next();
		if(names.hasNext() && main != null)
		{
			Function<MoParams, Object> function = this.functions.get(main);
			if(function != null)
			{
				Object struct = function.apply(environment.emptyParams);
				if(!(struct instanceof MoStruct))
				{
					throw new RuntimeException("Cannot set a value in query struct");
				} else
				{
					((MoStruct) struct).set(names, value);
				}
			} else
			{
				throw new RuntimeException("Cannot set a value in query struct");
			}
		} else
		{
			setValue(main, value);
		}
	}
	
	@Override
	public MoValue get(Iterator<String> names)
	{
		return get(names, environment.emptyParams);
	}
	
	@Override
	public void clear()
	{
		functions.clear();
	}
	
	@Override
	public void store(String key, double value)
	{
		setValue(key, value);
	}
	
	@Override
	public void store(String key, String value)
	{
		setValue(key, value);
	}
}