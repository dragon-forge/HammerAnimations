package dev.zeith.lzvm.molang.expression;

import dev.zeith.lzvm.molang.compiler.MoLangCompiler;
import dev.zeith.lzvm.op.LzOpcodes;
import dev.zeith.lzvm.program.*;

public class ContinueExpression
		extends MLExpression
{
	public static final ContinueExpression INSTANCE = new ContinueExpression();
	
	public ContinueExpression()
	{
		super(0);
	}
	
	@Override
	protected Object evalStatic()
	{
		return null;
	}
	
	@Override
	public void toLz(MoLangCompiler compiler, LzProgramBuilder builder, ExpressionScope scope)
	{
		scope.getJumpToLoopStart().accept(builder);
	}
	
	@Override
	public ArgType getExpectedLzType()
	{
		return null;
	}
}