package org.zeith.hammeranims.molang.runtime.struct;

import org.zeith.hammeranims.molang.runtime.MoLangEnvironment;
import org.zeith.hammeranims.molang.runtime.value.MoValue;

import java.util.*;

public class ContextStruct
		extends VariableStruct
{
	public ContextStruct(MoLangEnvironment environment)
	{
		super(environment);
	}
	
	public ContextStruct(MoLangEnvironment environment, Map<String, MoValue> map)
	{
		super(environment, map);
	}
	
	@Override
	public void set(Iterator<String> names, MoValue value)
	{
		String main = names.next();
		if(names.hasNext() && main != null)
		{
			MoValue struct = this.map.get(main);
			if(struct != null)
			{
				if(!(struct instanceof MoStruct))
				{
					throw new RuntimeException("Cannot set a value in context struct");
				} else
				{
					((MoStruct) struct).set(names, value);
				}
			} else
			{
				throw new RuntimeException("Cannot set a value in context struct");
			}
		} else
		{
			throw new RuntimeException("Cannot set a value in context struct");
		}
	}
}
