package org.zeith.hammeranims.molang.runtime.struct;

import com.google.common.collect.Iterators;
import lombok.Getter;
import org.zeith.hammeranims.molang.runtime.*;
import org.zeith.hammeranims.molang.runtime.value.*;

import java.util.*;

@Getter
public class VariableStruct
		implements MoStruct
{
	protected final MoLangEnvironment environment;
	protected final Map<String, MoValue> map;
	
	public VariableStruct(MoLangEnvironment environment)
	{
		this(environment, new HashMap<>());
	}
	
	public VariableStruct(MoLangEnvironment environment, Map<String, MoValue> map)
	{
		this.environment = environment;
		this.map = map;
	}
	
	public void set(String name, MoValue value)
	{
		this.map.put(name, value);
	}
	
	public void setValue(String name, Object value)
	{
		MoValue mo = MoValue.of(value);
		set(name, mo);
	}
	
	public void setValue(String[] name, Object value)
	{
		MoValue mo = MoValue.of(value);
		set(Iterators.forArray(name), mo);
	}
	
	public MoValue get(String name)
	{
		return this.map.getOrDefault(name, DoubleValue.ZERO);
	}
	
	@Override
	public void set(Iterator<String> names, MoValue value)
	{
		String main = names.next();
		if(names.hasNext() && main != null)
		{
			Object struct = this.map.get(main);
			if(!(struct instanceof MoStruct))
				struct = new VariableStruct(environment);
			((MoStruct) struct).set(names, value);
			this.map.put(main, (MoStruct) struct);
		} else
		{
			this.map.put(main, value);
		}
	}
	
	@Override
	public MoValue get(Iterator<String> names, MoParams params)
	{
		String main = names.next();
		if(names.hasNext() && main != null)
		{
			Object struct = this.map.get(main);
			if(struct instanceof MoStruct) return ((MoStruct) struct).get(names, params);
		}
		return this.map.getOrDefault(main, DoubleValue.ZERO);
	}
	
	@Override
	public MoValue get(Iterator<String> names)
	{
		return get(names, environment.emptyParams);
	}
	
	@Override
	public void clear()
	{
		map.clear();
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
