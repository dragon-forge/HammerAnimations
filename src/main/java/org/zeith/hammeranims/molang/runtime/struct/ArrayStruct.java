package org.zeith.hammeranims.molang.runtime.struct;

import org.zeith.hammeranims.molang.runtime.MoLangEnvironment;
import org.zeith.hammeranims.molang.runtime.value.MoValue;

import java.util.*;

public class ArrayStruct
		extends VariableStruct
{
	public ArrayStruct(MoLangEnvironment environment)
	{
		super(environment);
	}
	
	public ArrayStruct(MoLangEnvironment environment, Map<String, MoValue> map)
	{
		super(environment, map);
	}
	
	@Override
	public void set(Iterator<String> names, MoValue value)
	{
		ArrayList<String> namesList = new ArrayList<>();
		
		while(names.hasNext())
		{
			String name = names.next();
			if(!names.hasNext())
			{
				namesList.add(String.valueOf(Integer.parseInt(name)));
			} else
			{
				namesList.add(name);
			}
		}
		
		super.set(namesList.iterator(), value);
	}
}
