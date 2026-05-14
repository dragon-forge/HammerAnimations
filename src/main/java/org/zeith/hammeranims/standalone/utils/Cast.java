package org.zeith.hammeranims.standalone.utils;

import org.jetbrains.annotations.*;

import java.util.Optional;
import java.util.function.*;

public class Cast
{
	public static BooleanSupplier constantB(boolean value)
	{
		return () -> value;
	}
	
	public static DoubleSupplier constantD(double value)
	{
		return () -> value;
	}
	
	public static IntSupplier constantI(int value)
	{
		return () -> value;
	}
	
	public static LongSupplier constantL(long value)
	{
		return () -> value;
	}
	
	public static <T> Supplier<T> constant(T value)
	{
		return () -> value;
	}
	
	public static <T> Supplier<T> supply(Object thing, Class<T> type)
	{
		T result = cast(thing, type);
		return () -> result;
	}
	
	@NotNull
	public static <T> Optional<T> optionally(Object thing, Class<T> type)
	{
		return type.isInstance(thing) ? Optional.of(type.cast(thing)) : Optional.empty();
	}
	
	public static <IN, T> Function<IN, T> convertTo(Class<T> type)
	{
		return obj -> cast(obj, type);
	}
	
	public static <T> Optional<T> firstInstanceof(Class<T> type, Object... input)
	{
		for(Object i : input)
			if(i != null && type.isAssignableFrom(i.getClass()))
				return Optional.of(type.cast(i));
		return Optional.empty();
	}
	
	public static <T> T or(T... input)
	{
		for(T i : input)
			if(i != null)
				return i;
		return null;
	}
	
	@Nullable
	public static <T> T cast(Object thing, Class<T> type)
	{
		if(thing == null || type == null)
			return null;
		if(type.isAssignableFrom(thing.getClass()))
			return type.cast(thing);
		return null;
	}
	
	public static <T> T cast(Object thing)
	{
		try
		{
			return (T) thing;
		} catch(Throwable err)
		{
			return null;
		}
	}
}