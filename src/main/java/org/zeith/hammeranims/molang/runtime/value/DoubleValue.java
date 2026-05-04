package org.zeith.hammeranims.molang.runtime.value;

public record DoubleValue(double rawValue)
		implements MoValue
{
	public final static DoubleValue ZERO = new DoubleValue(0.0);
	public final static DoubleValue ONE = new DoubleValue(1.0);
	
	public DoubleValue(Object value)
	{
		this(value instanceof Boolean
			 ? ((boolean) value ? 1.0 : 0.0)
			 : value instanceof Number
			   ? ((Number) value).doubleValue()
			   : value instanceof DoubleValue
				 ? ((DoubleValue) value).rawValue()
				 : 1.0
		);
	}
	
	@Override
	public Double value()
	{
		return rawValue;
	}
	
	@Override
	public String asString()
	{
		return Double.toString(rawValue);
	}
	
	@Override
	public double asDouble()
	{
		return rawValue;
	}
}
