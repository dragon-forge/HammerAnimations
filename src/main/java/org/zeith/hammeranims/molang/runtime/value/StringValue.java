package org.zeith.hammeranims.molang.runtime.value;

public record StringValue(String value)
		implements MoValue
{
	@Override
	public String asString()
	{
		return value;
	}
}
