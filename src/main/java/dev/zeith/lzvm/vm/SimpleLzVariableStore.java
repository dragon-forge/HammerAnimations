package dev.zeith.lzvm.vm;

import dev.zeith.lzvm.LzVariableStore;
import dev.zeith.lzvm.op.*;
import dev.zeith.lzvm.program.LzCallInsn;

import java.util.*;

public class SimpleLzVariableStore
		implements LzVariableStore
{
	protected final Map<String, LzCallOp> callRegister = new HashMap<>();
	protected final Map<String, LzVarOp> varRegister = new HashMap<>();
	
	public SimpleLzVariableStore registerCall(LzCallInsn name, LzCallOp op)
	{
		callRegister.put(name.name + name.descriptor, op);
		return this;
	}
	
	public SimpleLzVariableStore registerVar(String name, LzVarOp op)
	{
		varRegister.put(name, op);
		return this;
	}
	
	public SimpleLzVariableStore registerReadVar(String name, ReadonlyLzVarOp op)
	{
		varRegister.put(name, op);
		return this;
	}
	
	@Override
	public LzCallOp findCall(String name, String descriptor)
	{
		return callRegister.getOrDefault(name + descriptor, LzCallOp.NO_OP);
	}
	
	@Override
	public LzVarOp findVar(String name)
	{
		return varRegister.getOrDefault(name, LzVarOp.ZERO);
	}
	
	@Override
	public LzVarOp tempVar(String name)
	{
		return LzVarOp.readWrite();
	}
}