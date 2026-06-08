package org.zeith.hammeranims.api.animation.interp;

import dev.zeith.lzvm.exception.LzVMOperationNotSupportedException;
import dev.zeith.lzvm.op.LzVarOp;

public class ReadWriteVariable
		implements LzVarOp
{
	double val;
	
	public ReadWriteVariable(double val)
	{
		this.val = val;
	}
	
	public ReadWriteVariable()
	{
	}
	
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
	public void reset() {}
	
	@Override
	public String toString()
	{
		return "rw(" + val + ")";
	}
}