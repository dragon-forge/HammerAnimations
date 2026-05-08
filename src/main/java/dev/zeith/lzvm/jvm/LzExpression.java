package dev.zeith.lzvm.jvm;

import org.zeith.hammeranims.HammerAnimations;

public interface LzExpression
		extends LzFactory
{
	LzExpression[] EMPTY_EXPRESSION = new LzExpression[0];
	double[] EMPTY_DOUBLE_ARRAY = new double[0];
	
	default double get()
	{
		try
		{
			return get(EMPTY_DOUBLE_ARRAY);
		} catch(IncompatibleClassChangeError e)
		{
			HammerAnimations.LOG.error("Failed to call get() on {}", this);
			throw e;
		}
	}
	
	double get(double... args);
}