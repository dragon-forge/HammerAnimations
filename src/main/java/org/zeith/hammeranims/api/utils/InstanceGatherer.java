package org.zeith.hammeranims.api.utils;

import java.util.*;

public class InstanceGatherer
{
	public static <B, T extends B> List<T> getComponents(Iterable<B> base, Class<T> clazz)
	{
		List<T> list = new ArrayList<>();
		for(B component : base)
			if(clazz.isAssignableFrom(component.getClass()))
				list.add(clazz.cast(component));
		return Collections.unmodifiableList(list);
	}
	
	public static <B, T extends B> T get(Iterable<B> base, Class<T> clazz)
	{
		for(B component : base)
			if(clazz.isInstance(component))
				return clazz.cast(component);
		return null;
	}
}