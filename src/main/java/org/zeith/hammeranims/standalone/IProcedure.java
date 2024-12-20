package org.zeith.hammeranims.standalone;

import org.zeith.hammeranims.standalone.utils.Args;

public interface IProcedure
{
	void run();
	
	default void dispose() {}
	
	default String requireArg(Args args, String arg, ArgPredicate predicate)
	{
		String s = args.getString(arg);
		if(s == null)
		{
			if(predicate != null) problem("Missing -" + arg + " (" + predicate.requirements() + ")");
			problem("Missing -" + arg);
		}
		if(predicate != null && !predicate.validator().test(s)) problem("Validation failed on -" + arg + " (" + s + "): " + predicate.requirements());
		return s;
	}
	
	default <T> T requireArg(Args args, String arg, ArgTransformer<T> predicate)
	{
		String s = args.getString(arg);
		if(s == null)
		{
			if(predicate != null) problem("Missing -" + arg + " (" + predicate.requirements() + ")");
			problem("Missing -" + arg);
		}
		T apply = predicate.validator().apply(s);
		if(apply == null) problem("Validation failed on -" + arg + " (" + s + "): " + predicate.requirements());
		return apply;
	}
	
	default void problem(String message)
	{
		throw new ConfigureException(message);
	}
	
	default void problem(String message, Throwable cause)
	{
		throw new ConfigureException(message, cause);
	}
	
	class ConfigureException
			extends RuntimeException
	{
		public ConfigureException(String message)
		{
			super(message);
		}
		
		public ConfigureException(String message, Throwable cause)
		{
			super(message, cause);
		}
	}
}