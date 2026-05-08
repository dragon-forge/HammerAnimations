package dev.zeith.lzvm.molang.expression;

import dev.zeith.lzvm.molang.compiler.MoLangCompiler;
import dev.zeith.lzvm.program.*;
import lombok.ToString;

import java.util.function.Consumer;

@ToString
public class NameExpression
		extends MLExpression
		implements IVarAccessExpression
{
	public final String name;
	
	public NameExpression(String name)
	{
		super(0);
		this.name = name;
	}
	
	@Override
	protected boolean isOptimized()
	{
		return false;
	}
	
	@Override
	protected Object evalStatic()
	{
		return null;
	}
	
	@Override
	public void toLz(MoLangCompiler compiler, LzProgramBuilder builder, ExpressionScope scope)
	{
		MLExpression tf = compiler.findNameTransformer(this);
		if(tf != this) tf.toLz(compiler, builder, scope);
		else builder.addRead(this.name);
	}
	
	@Override
	public ArgType getExpectedLzType()
	{
		return ArgType.DOUBLE;
	}
	
	@Override
	public void addWrite(MoLangCompiler compiler, LzProgramBuilder builder, ExpressionScope scope, Consumer<LzProgramBuilder> whatToWrite)
	{
		whatToWrite.accept(builder);
		builder.addWrite(this.name);
	}
	
	@Override
	public MLExpression optimizeStatic(MoLangCompiler compiler)
	{
		MLExpression tf = compiler.findNameTransformer(this);
		if(tf != this) return tf;
		return super.optimizeStatic(compiler);
	}
}