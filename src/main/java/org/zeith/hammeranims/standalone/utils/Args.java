package org.zeith.hammeranims.standalone.utils;

import java.util.HashMap;
import java.util.Map;

public class Args
{
	private final Map<String, Object> arguments;
	
	public Args(String[] args)
	{
		Map<String, Object> arguments = new HashMap<>();
		
		for(int i = 0; i < args.length; i++)
		{
			String current = args[i];
			
			if(current.startsWith("-"))
			{
				String name = current.substring(1);
				
				if(i + 1 >= args.length || args[i + 1].startsWith("-"))
				{
					arguments.put(name, true);
					continue;
				}
				
				arguments.put(name, args[i + 1]);
				++i;
			}
		}
		this.arguments = Map.copyOf(arguments);
	}
	
	public Object get(String key)
	{
		return arguments.get(key);
	}
	
	public boolean hasKey(String key)
	{
		return arguments.containsKey(key);
	}
	
	public String getString(String key)
	{
		Object o = get(key);
		return o == null ? null : o.toString();
	}
}