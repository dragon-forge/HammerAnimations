package org.zeith.hammeranims.api.animation.interp;

import dev.zeith.lzvm.LzVariableStore;
import dev.zeith.lzvm.op.*;

public interface IVariableAccess
		extends LzVariableStore
{
	void setVariable(String name, LzVarOp value);
	
	@Override
	default LzCallOp findCall(String name, String descriptor)
	{
		return LzCallOp.NO_OP;
	}
	
	@Override
	default LzVarOp tempVar(String name)
	{
		return LzVarOp.tempVar();
	}
}