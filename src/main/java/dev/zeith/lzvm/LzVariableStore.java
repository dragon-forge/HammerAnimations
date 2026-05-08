package dev.zeith.lzvm;

import dev.zeith.lzvm.op.*;
import dev.zeith.lzvm.program.LzCallInsn;

public interface LzVariableStore
{
	default LzCallOp findCallByName(LzCallInsn insn)
	{
		return findCall(insn.name, insn.descriptor);
	}
	
	LzCallOp findCall(String name, String descriptor);
	
	LzVarOp findVar(String name);
	
	LzVarOp tempVar(String name);
}