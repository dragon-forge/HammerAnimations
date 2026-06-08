package org.zeith.hammeranims.api.animation.interp;

import dev.zeith.lzvm.op.LzVarOp;

import java.util.*;

public abstract class BaseQuery
		implements IVariableAccess
{
	protected final Map<String, LzVarOp> vars = new HashMap<>();
	
	public BaseQuery()
	{
		registerVariables(IVariableRegistrar.of(this));
	}
	
	protected void registerVariables(IVariableRegistrar reg)
	{
	}
	
	public ReadWriteVariable getOrCreateRWVar(String name, double defaultValue)
	{
		return (ReadWriteVariable) vars.computeIfAbsent(name, l ->
				{
					ReadWriteVariable v = new ReadWriteVariable();
					v.val = defaultValue;
					return v;
				}
		);
	}
	
	@Override
	public LzVarOp findVar(String name)
	{
		return vars.computeIfAbsent(name, l -> new ReadWriteVariable());
	}
	
	@Override
	public void setVariable(String name, LzVarOp value)
	{
		vars.put(name, value);
	}
}