package org.zeith.hammeranims.api.animation.interp;

import dev.zeith.lzvm.exception.LzVMOperationNotSupportedException;
import dev.zeith.lzvm.op.*;
import lombok.Setter;

import java.util.*;
import java.util.function.BiConsumer;

public abstract class BaseQuery
		implements IVariableAccess
{
	protected final Map<String, LzVarOp> vars = new HashMap<>();
	
	public BaseQuery()
	{
		registerVariables(this::setVariable);
	}
	
	protected void registerVariables(BiConsumer<String, ReadonlyLzVarOp> reg)
	{
	}
	
	public ReadWriteVariable getOrCreateRWVar(String name, double defaultValue)
	{
		return (ReadWriteVariable) vars.computeIfAbsent(name, l ->
				{
					ReadWriteVariable v = new ReadWriteVariable();
					v.val = v.defaultValue = defaultValue;
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
	
	public static class ReadWriteVariable
			implements LzVarOp
	{
		@Setter
		double defaultValue;
		
		double val;
		
		
		@Override
		public double get()
				throws LzVMOperationNotSupportedException
		{
			return val;
		}
		
		@Override
		public void set(double value)
				throws LzVMOperationNotSupportedException
		{
			val = value;
		}
		
		@Override
		public void reset()
		{
			val = defaultValue;
		}
		
		@Override
		public String toString()
		{
			return "rw(" + val + ")";
		}
	}
}
