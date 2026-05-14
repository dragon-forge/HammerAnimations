package dev.zeith.lzvm.op;

import dev.zeith.lzvm.exception.LzVMOperationNotSupportedException;

import java.util.function.*;

public interface LzVarOp
{
	LzVarOp ZERO = readOnly(() -> 0);
	
	static LzVarOp readOnly(ReadonlyLzVarOp getter)
	{
		return getter;
	}
	
	static LzVarOp tempVar()
	{
		return readWrite();
	}
	
	static LzVarOp readWrite(DoubleSupplier get, DoubleConsumer set)
	{
		return new LzVarOp()
		{
			@Override
			public double get()
					throws LzVMOperationNotSupportedException
			{
				return get.getAsDouble();
			}
			
			@Override
			public void set(double value)
					throws LzVMOperationNotSupportedException
			{
				set.accept(value);
			}
		};
	}
	
	static LzVarOp readWrite()
	{
		return readWrite(0);
	}
	
	static LzVarOp readWrite(double defaultValue)
	{
		return new LzVarOp()
		{
			double v = defaultValue;
			
			@Override
			public double get()
					throws LzVMOperationNotSupportedException
			{
				return v;
			}
			
			@Override
			public void set(double value)
					throws LzVMOperationNotSupportedException
			{
				v = value;
			}
			
			@Override
			public void reset()
			{
				v = defaultValue;
			}
			
			@Override
			public String toString()
			{
				return "rw(" + v + ")";
			}
		};
	}
	
	default void reset() {}
	
	double get()
			throws LzVMOperationNotSupportedException;
	
	void set(double value)
			throws LzVMOperationNotSupportedException;
	
	default double get(double index)
	{
		throw new LzVMOperationNotSupportedException();
	}
	
	default void set(double index, double value)
	{
		throw new LzVMOperationNotSupportedException();
	}
}