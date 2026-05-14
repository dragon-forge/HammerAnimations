package dev.zeith.lzvm.molang.expression;

import dev.zeith.lzvm.molang.compiler.MoLangCompiler;
import dev.zeith.lzvm.program.*;

public class AssignExpression
		extends MLExpression
{
	protected final IVarAccessExpression name;
	
	public AssignExpression(IVarAccessExpression name, MLExpression value)
	{
		super(2);
		this.name = name;
		this.children[0] = (MLExpression) name;
		this.children[1] = value;
	}
	
	@Override
	protected Object evalStatic()
	{
		return null;
	}
	
	@Override
	public void toLz(MoLangCompiler compiler, LzProgramBuilder builder, ExpressionScope scope)
	{
		// Write value onto the stack
		name.addWrite(compiler, builder, scope, (b) ->
				// Push the value onto the stack
				this.children[1].toLz(compiler, b, scope)
		);
	}
	
	@Override
	public ArgType getExpectedLzType()
	{
		return null;
	}
}