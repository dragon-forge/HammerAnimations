package org.zeith.hammeranims.standalone.utils;

public class MoreObjects
{
	public static <T> T firstNonNull(T first, T second)
	{
		if(first != null)
		{
			return first;
		}
		if(second != null)
		{
			return second;
		}
		throw new NullPointerException("Both parameters are null");
	}
}