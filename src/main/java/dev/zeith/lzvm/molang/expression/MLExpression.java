package dev.zeith.lzvm.molang.expression;

import dev.zeith.lzvm.molang.compiler.MoLangCompiler;
import dev.zeith.lzvm.program.*;
import lombok.Getter;

import java.util.OptionalDouble;

public abstract class MLExpression
{
	public static final MLExpression[] EMPTY_ARRAY = new MLExpression[0];
	
	@Getter
	protected final MLExpression[] children;
	
	public MLExpression(int childrenCount)
	{
		this.children = childrenCount == 0 ? EMPTY_ARRAY : new MLExpression[childrenCount];
	}
	
	protected void setExpression(int index, MLExpression expr)
	{
		this.children[index] = expr;
	}
	
	protected boolean isOptimized()
	{
		return false;
	}
	
	protected boolean isStatic()
	{
		for(MLExpression child : children)
			if(child != null && !child.isStatic())
				return false;
		return true;
	}
	
	protected abstract Object evalStatic();
	
	public abstract void toLz(MoLangCompiler compiler, LzProgramBuilder builder, ExpressionScope scope);
	
	public MLExpression optimizeStatic(MoLangCompiler compiler)
	{
		if(isOptimized()) return this;
		for(int i = 0, len = children.length; i < len; i++)
		{
			MLExpression child = children[i];
			if(child != null)
			{
				MLExpression optChild = child.optimizeStatic(compiler);
				if(child != optChild) children[i] = optChild;
			}
		}
		if(getExpectedLzType() == ArgType.DOUBLE)
		{
			Object v = evalStatic();
			if(v instanceof Number) return new NumberExpression(((Number) v).doubleValue());
		}
		return this;
	}
	
	public abstract ArgType getExpectedLzType();
	
	public OptionalDouble asOptimizedDouble()
	{
		Object st = evalStatic();
		if(st instanceof Number) return OptionalDouble.of(((Number) st).doubleValue());
		return OptionalDouble.empty();
	}
}