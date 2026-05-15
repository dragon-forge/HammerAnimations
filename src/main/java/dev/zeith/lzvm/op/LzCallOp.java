package dev.zeith.lzvm.op;

public interface LzCallOp
{
	LzCallOp NO_OP = args -> 0.0;
	
	double call(Object[] args);
}