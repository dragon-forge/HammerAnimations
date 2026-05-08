package dev.zeith.lzvm.molang.expression;

import dev.zeith.lzvm.molang.compiler.MoLangCompiler;
import dev.zeith.lzvm.op.LzOpcodes;
import dev.zeith.lzvm.program.*;

public class NegateExpression
		extends MLExpression
{
	public NegateExpression(MLExpression child)
	{
		super(1);
		this.children[0] = child;
	}
	
	@Override
	protected Object evalStatic()
	{
		Object o = children[0].evalStatic();
		if(o instanceof Number)
			return -((Number) o).doubleValue();
		return null;
	}
	
	@Override
	public void toLz(MoLangCompiler compiler, LzProgramBuilder builder, ExpressionScope scope)
	{
		children[0].toLz(compiler, builder, scope);
		builder.addConstD(-1);
		builder.addInsn(LzOpcodes.MUL);
	}
	
	@Override
	public ArgType getExpectedLzType()
	{
		return ArgType.DOUBLE;
	}
}