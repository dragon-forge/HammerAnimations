package dev.zeith.lzvm.molang.expression;

import dev.zeith.lzvm.molang.compiler.MoLangCompiler;
import dev.zeith.lzvm.program.*;
import lombok.ToString;

@ToString
public class StringExpression
		extends MLExpression
{
	protected final String value;
	
	public StringExpression(String value)
	{
		super(0);
		this.value = value;
	}
	
	@Override
	protected boolean isOptimized()
	{
		return true;
	}
	
	@Override
	protected boolean isStatic()
	{
		return true;
	}
	
	@Override
	protected Object evalStatic()
	{
		return value;
	}
	
	@Override
	public void toLz(MoLangCompiler compiler, LzProgramBuilder builder, ExpressionScope scope)
	{
		builder.addConstS(this.value);
	}
	
	@Override
	public ArgType getExpectedLzType()
	{
		return ArgType.STRING;
	}
}
