package dev.zeith.lzvm.molang.expression;

import dev.zeith.lzvm.molang.compiler.MoLangCompiler;
import dev.zeith.lzvm.program.*;

public class CodeBlockExpression
		extends MLExpression
{
	public CodeBlockExpression(MLExpression[] children)
	{
		super(children.length);
		System.arraycopy(children, 0, this.children, 0, children.length);
	}
	
	@Override
	protected Object evalStatic()
	{
		return null;
	}
	
	@Override
	public void toLz(MoLangCompiler compiler, LzProgramBuilder builder, ExpressionScope scope)
	{
		for(MLExpression child : this.children)
		{
			child.toLz(compiler, builder, scope);
		}
	}
	
	@Override
	public ArgType getExpectedLzType()
	{
		return null;
	}
}