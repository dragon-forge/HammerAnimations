package dev.zeith.lzvm.op;

import dev.zeith.lzvm.exception.LzVMOperationNotSupportedException;

import java.util.function.BooleanSupplier;

public interface ReadonlyLzVarOp
		extends LzVarOp
{
	static ReadonlyLzVarOp ofBool(BooleanSupplier bool)
	{
		return () -> bool.getAsBoolean() ? 1 : 0;
	}
	
	@Override
	default void set(double value)
			throws LzVMOperationNotSupportedException
	{
		throw new LzVMOperationNotSupportedException();
	}
}
