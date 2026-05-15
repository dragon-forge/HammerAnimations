package dev.zeith.lzvm.molang.expression;

import dev.zeith.lzvm.molang.compiler.MoLangCompiler;
import dev.zeith.lzvm.program.*;

import java.util.function.Consumer;

public class ArrayExpression
		extends MLExpression
		implements IVarAccessExpression
{
	public final String name;
	
	public ArrayExpression(NameExpression varName, MLExpression index)
	{
		super(2);
		this.name = varName.name;
		this.children[0] = varName;
		this.children[1] = index;
	}
	
	@Override
	public MLExpression optimizeStatic(MoLangCompiler compiler)
	{
		return super.optimizeStatic(compiler);
	}
	
	@Override
	protected Object evalStatic()
	{
		return null;
	}
	
	@Override
	public void toLz(MoLangCompiler compiler, LzProgramBuilder builder, ExpressionScope scope)
	{
		this.children[1].toLz(compiler, builder, scope);
		builder.addArrayRead(name);
	}
	
	@Override
	public ArgType getExpectedLzType()
	{
		return ArgType.DOUBLE;
	}
	
	@Override
	public void addWrite(MoLangCompiler compiler, LzProgramBuilder builder, ExpressionScope scope, Consumer<LzProgramBuilder> whatToWrite)
	{
		// Push index onto the stack first
		this.children[1].toLz(compiler, builder, scope);
		
		// Push value onto the stack
		whatToWrite.accept(builder);
		
		builder.addArrayWrite(name);
	}
}