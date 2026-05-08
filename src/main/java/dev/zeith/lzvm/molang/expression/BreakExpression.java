package dev.zeith.lzvm.molang.expression;

import dev.zeith.lzvm.molang.compiler.MoLangCompiler;
import dev.zeith.lzvm.program.*;

public class BreakExpression
		extends MLExpression
{
	public static final BreakExpression INSTANCE = new BreakExpression();
	
	public BreakExpression()
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
		scope.getJumpToLoopExit().accept(builder);
	}
	
	@Override
	public ArgType getExpectedLzType()
	{
		return null;
	}
}