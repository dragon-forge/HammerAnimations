package org.zeith.hammeranims.standalone.utils;

import org.zeith.hammeranims.standalone.utils.base.Cast;
import org.zeith.hammeranims.HammerAnimations;

public class ReflectionUtil
{
	public static <T> Class<T> fetchClass(String name)
	{
		try
		{
			return Cast.cast(Class.forName(name));
		} catch(ClassNotFoundException ignored)
		{
		} catch(RuntimeException e)
		{
			if(e.getMessage().contains("invalid dist"))
			{
				HammerAnimations.LOG.warn("Attempted to load class from invalid dist: " + name, e);
			}
		}
		return null;
	}
}