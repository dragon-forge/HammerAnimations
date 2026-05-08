package org.zeith.hammeranims.api.animation.interp;

import dev.zeith.lzvm.LzVariableStore;
import dev.zeith.lzvm.jvm.*;

import java.util.Arrays;

public class DoubleInterpolation
		extends BaseInterpolation
{
	public static final DoubleInterpolation ZERO = new DoubleInterpolation(InterpolatedDouble.constant(0));
	public static final DoubleInterpolation ONE = new DoubleInterpolation(InterpolatedDouble.constant(1));
	
	protected final LzFactory[] doubles;
	
	public DoubleInterpolation(LzFactory... doubles)
	{
		this.doubles = doubles;
	}
	
	@Override
	public int getDoubleCount()
	{
		return doubles.length;
	}
	
	@Override
	public LzExpression[] instantiate(LzVariableStore query)
	{
		return LzFactory.instantiate(query, doubles);
	}
	
	@Override
	public String toString()
	{
		return "DoubleInterpolation" + Arrays.toString(doubles);
	}
}