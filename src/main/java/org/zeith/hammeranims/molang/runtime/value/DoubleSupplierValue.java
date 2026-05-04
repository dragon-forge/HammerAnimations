package org.zeith.hammeranims.molang.runtime.value;

import java.util.function.DoubleSupplier;

public record DoubleSupplierValue(DoubleSupplier rawValue)
		implements MoValue
{
	@Override
	public Double value()
	{
		return rawValue.getAsDouble();
	}
	
	@Override
	public String asString()
	{
		return Double.toString(rawValue.getAsDouble());
	}
	
	@Override
	public double asDouble()
	{
		return rawValue.getAsDouble();
	}
}
