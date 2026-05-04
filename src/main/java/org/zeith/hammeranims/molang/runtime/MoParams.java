package org.zeith.hammeranims.molang.runtime;

import org.zeith.hammeranims.molang.runtime.struct.MoStruct;
import org.zeith.hammeranims.molang.runtime.value.*;

import java.util.List;

public record MoParams(MoLangEnvironment environment, List<MoValue> params)
{
	@SuppressWarnings("unchecked")
	public <T extends MoValue> T get(int index)
	{
		return (T) params.get(index);
	}
	
	public boolean contains(int index)
	{
		return params.size() >= index + 1;
	}
	
	public int getInt(int index)
	{
		return (int) getDouble(index);
	}
	
	public double getDouble(int index)
	{
		return this.<DoubleValue>get(index).asDouble();
	}
	
	public MoStruct getStruct(int index)
	{
		return get(index);
	}
	
	public String getString(int index)
	{
		return this.<StringValue>get(index).asString();
	}
	
	public MoLangEnvironment getEnvironment(int index)
	{
		return get(index);
	}
}