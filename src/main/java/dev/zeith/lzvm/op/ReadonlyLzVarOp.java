package dev.zeith.lzvm.op;

import dev.zeith.lzvm.exception.LzVMOperationNotSupportedException;

public interface ReadonlyLzVarOp
		extends LzVarOp
{
	@Override
	default void set(double value)
			throws LzVMOperationNotSupportedException
	{
		throw new LzVMOperationNotSupportedException();
	}
}
