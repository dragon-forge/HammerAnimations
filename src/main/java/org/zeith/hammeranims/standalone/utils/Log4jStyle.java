package org.zeith.hammeranims.standalone.utils;

import org.zeith.libs.tuples.*;

import java.util.*;
import java.util.function.Consumer;

public class Log4jStyle
{
	public static void print(Consumer<String> out, String level, String message, Object... e)
	{
		Tuple2<String, List<Throwable>> res = catchAndFormat(message, e);
		out.accept(res.a());
		res.b().forEach(err ->
				out.accept(err.getClass().getName() + ": " + err.getMessage())
		);
	}
	
	public static Tuple2<String, List<Throwable>> catchAndFormat(String message, Object... e)
	{
		List<Throwable> errors = new ArrayList<>();
		e = Arrays.stream(e).filter(o ->
		{
			if(o instanceof Throwable err)
			{
				errors.add(err);
				return false;
			}
			return true;
		}).toArray(Object[]::new);
		return Tuples.immutable(of(message, e), errors);
	}
	
	public static String of(String message, Object... args)
	{
		for(Object arg : args)
			message = message.replaceFirst("\\{}", Objects.toString(arg));
		return message;
	}
}