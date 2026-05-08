package dev.zeith.lzvm.molang.expression;

import dev.zeith.lzvm.molang.compiler.MoLangCompiler;
import dev.zeith.lzvm.op.LzOpcodes;
import dev.zeith.lzvm.program.*;

public class ReturnExpression
		extends MLExpression
{
	public ReturnExpression(MLExpression expr)
	{
		super(1);
		this.children[0] = expr;
	}
	
	@Override
	protected Object evalStatic()
	{
		return null;
	}
	
	@Override
	public void toLz(MoLangCompiler compiler, LzProgramBuilder builder, ExpressionScope scope)
	{
		children[0].toLz(compiler, builder, scope);
		builder.addInsn(LzOpcodes.RETURN);
	}
	
	@Override
	public ArgType getExpectedLzType()
	{
		return null;
	}
}
