package org.zeith.hammeranims.api.animation.interp;

import dev.zeith.lzvm.op.*;

public interface IVariableRegistrar
{
	void registerVar(String name, LzVarOp var);
	
	default void registerR(String name, ReadonlyLzVarOp var)
	{
		registerVar(name, var);
	}
	
	default void registerRW(String name, ReadWriteVariable var)
	{
		registerVar(name, var);
	}
	
	static IVariableRegistrar of(BaseQuery q)
	{
		return q::setVariable;
	}
}