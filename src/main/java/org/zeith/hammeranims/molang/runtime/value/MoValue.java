package org.zeith.hammeranims.molang.runtime.value;

public interface MoValue
{
	static MoValue of(Object value)
	{
		if(value instanceof MoValue)
		{
			return (MoValue) value;
		} else if(value instanceof CharSequence cs)
		{
			return new StringValue(cs.toString());
		} else
		{
			return new DoubleValue(value);
		}
	}
	
	Object value();
	
	default String asString()
	{
		return this.toString();
	}
	
	default double asDouble()
	{
		return 1.0;
	}
}
