package dev.zeith.lzvm.molang.expression;

import dev.zeith.lzvm.jvm.LzMath;
import dev.zeith.lzvm.molang.compiler.MoLangCompiler;
import dev.zeith.lzvm.op.LzOpcodes;
import dev.zeith.lzvm.program.*;

public class NotExpression
		extends MLExpression
{
	public NotExpression(MLExpression child)
	{
		super(1);
		this.children[0] = child;
	}
	
	@Override
	protected Object evalStatic()
	{
		Object o = children[0].evalStatic();
		if(o instanceof Number) return LzMath.notd(((Number) o).doubleValue());
		return null;
	}
	
	@Override
	public void toLz(MoLangCompiler compiler, LzProgramBuilder builder, ExpressionScope scope)
	{
		children[0].toLz(compiler, builder, scope);
		builder.addInsn(LzOpcodes.NOT);
	}
	
	@Override
	public MLExpression optimizeStatic(MoLangCompiler compiler)
	{
		super.optimizeStatic(compiler);
		Object staticValue = evalStatic();
		if(staticValue instanceof Number)
			return new NumberExpression(((Number) staticValue).doubleValue());
		return this;
	}
	
	@Override
	public ArgType getExpectedLzType()
	{
		return ArgType.DOUBLE;
	}
}