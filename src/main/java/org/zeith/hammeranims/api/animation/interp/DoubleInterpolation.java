package org.zeith.hammeranims.api.animation.interp;

import java.util.Arrays;

public class DoubleInterpolation
		extends BaseInterpolation
{
	protected final InterpolatedDouble[] doubles;
	
	public DoubleInterpolation(InterpolatedDouble... doubles)
	{
		this.doubles = doubles;
	}
	
	@Override
	public int getDoubleCount()
	{
		return doubles.length;
	}
	
	@Override
	public double[] get(Query q)
	{
		double[] arr = new double[doubles.length];
		for(int i = 0; i < arr.length; i++) arr[i] = doubles[i].get(q);
		return arr;
	}
	
	@Override
	public String toString()
	{
		return "DoubleInterpolation" + Arrays.toString(doubles);
	}
}