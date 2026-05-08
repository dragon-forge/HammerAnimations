package dev.zeith.lzvm.molang.expression;

import dev.zeith.lzvm.exception.LzVMException;
import dev.zeith.lzvm.molang.compiler.*;
import dev.zeith.lzvm.program.*;

import java.util.*;
import java.util.stream.Collectors;

public class FuncCallExpression
		extends MLExpression
{
	public final NameExpression name;
	
	public FuncCallExpression(NameExpression name, MLExpression... args)
	{
		super(args.length);
		System.arraycopy(args, 0, this.getChildren(), 0, args.length);
		this.name = name;
	}
	
	@Override
	public String toString()
	{
		return name + "(" + Arrays.stream(this.getChildren()).map(MLExpression::toString).collect(Collectors.joining(", ")) + ")";
	}
	
	@Override
	protected Object evalStatic()
	{
		return null;
	}
	
	public LzCallInsn determineCallDesc()
	{
		MLExpression[] expressions = this.children;
		
		// Push all args onto the stack first
		ArgType[] argumentTypes = new ArgType[this.children.length];
		for(int i = 0, len = expressions.length; i < len; i++) argumentTypes[i] = expressions[i].getExpectedLzType();
		
		return new LzCallInsn(name.name.toLowerCase(Locale.ROOT), ArgType.DOUBLE, argumentTypes);
	}
	
	@Override
	public void toLz(MoLangCompiler compiler, LzProgramBuilder builder, ExpressionScope scope)
	{
		LzCallInsn call = determineCallDesc();
		
		// Potentially transform things like math.sin to convert degrees to radians first and then call optimized fsin instruction:
		IMoFunctionCallTransformer trf = compiler.findCallTransformer(call);
		if(trf != null && trf.appendCall(compiler, builder, this, call, scope)) return;
		
		// Standard approach
		for(MLExpression expression : this.children) expression.toLz(compiler, builder, scope);
		builder.addCall(call);
	}
	
	@Override
	public MLExpression optimizeStatic(MoLangCompiler compiler)
	{
		super.optimizeStatic(compiler);
		
		LzCallInsn call = determineCallDesc();
		
		// Potentially transform things like math.sin to convert degrees to radians first and then call optimized fsin instruction:
		IMoFunctionCallTransformer trf = compiler.findCallTransformer(call);
		
		try
		{
			return trf != null && trf.isPure() ? trf.optimizeCall(compiler, this, call) : this;
		} catch(Exception e)
		{
			throw new LzVMException("Failed to optimize " + this, e);
		}
	}
	
	@Override
	public ArgType getExpectedLzType()
	{
		return ArgType.DOUBLE;
	}
}