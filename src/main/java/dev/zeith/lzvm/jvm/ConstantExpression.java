package dev.zeith.lzvm.jvm;

import dev.zeith.lzvm.LzVariableStore;

public class ConstantExpression
		implements LzExpression
{
	protected final double value;
	
	public ConstantExpression(double value)
	{
		this.value = value;
	}
	
	@Override
	public double get(double... args)
	{
		return value;
	}
	
	@Override
	public LzExpression instantiate(LzVariableStore store)
	{
		return this;
	}
}
