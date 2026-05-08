package dev.zeith.lzvm.molang.expression;

import dev.zeith.lzvm.molang.compiler.MoLangCompiler;
import dev.zeith.lzvm.program.*;
import lombok.ToString;

@ToString
public class NumberExpression
		extends MLExpression
{
	protected final double value;
	
	public static final NumberExpression ZERO = new NumberExpression(0);
	public static final NumberExpression ONE = new NumberExpression(1);
	
	public NumberExpression(double value)
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
		builder.addConstD(this.value);
	}
	
	@Override
	public ArgType getExpectedLzType()
	{
		return ArgType.DOUBLE;
	}
}
