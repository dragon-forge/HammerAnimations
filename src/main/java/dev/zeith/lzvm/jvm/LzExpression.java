package dev.zeith.lzvm.jvm;

public interface LzExpression
		extends LzFactory
{
	LzExpression[] EMPTY_EXPRESSION = new LzExpression[0];
	double[] EMPTY_DOUBLE_ARRAY = new double[0];
	
	default double get()
	{
		return get(EMPTY_DOUBLE_ARRAY);
	}
	
	double get(double... args);
}