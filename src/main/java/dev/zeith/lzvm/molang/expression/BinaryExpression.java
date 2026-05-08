package dev.zeith.lzvm.molang.expression;

import dev.zeith.lzvm.jvm.LzMath;
import dev.zeith.lzvm.molang.compiler.MoLangCompiler;
import dev.zeith.lzvm.op.*;
import dev.zeith.lzvm.program.*;
import lombok.Getter;

public class BinaryExpression
		extends MLExpression
{
	@Getter
	public final LzBinaryOp operation;
	
	public BinaryExpression(LzBinaryOp binOp, MLExpression left, MLExpression right)
	{
		super(2);
		this.operation = binOp;
		this.children[0] = left;
		this.children[1] = right;
	}
	
	@Override
	public String toString()
	{
		return "BinaryExpression{" + children[0].toString() + " " + operation.getAsString() + " " + children[1].toString() + "}";
	}
	
	@Override
	protected Object evalStatic()
	{
		Object left = children[0] != null ? children[0].evalStatic() : null;
		Object right = children[1] != null ? children[1].evalStatic() : null;
		if(left instanceof Number && right instanceof Number)
			return operation.operate(
					((Number) left).doubleValue(),
					((Number) right).doubleValue()
			);
		if((left instanceof String || right instanceof String) && (left != null && right != null) && operation == LzBinaryOp.ADD)
			return left.toString() + right.toString();
		return null;
	}
	
	@Override
	public MLExpression optimizeStatic(MoLangCompiler compiler)
	{
		super.optimizeStatic(compiler);
		
		Object staticValue = evalStatic();
		if(staticValue instanceof Number)
			return new NumberExpression(((Number) staticValue).doubleValue());
		if(staticValue instanceof String)
			return new StringExpression((String) staticValue);
		
		if(operation == LzBinaryOp.AND)
		{
			if(isNonZero(0) || isNonZero(1))
				return NumberExpression.ZERO;
		}
		
		if(operation == LzBinaryOp.OR)
		{
			if(isNonZero(0) || isNonZero(1))
				return NumberExpression.ONE;
		}
		
		if(operation == LzBinaryOp.ADD)
		{
			if(isZero(0))
				return children[1];
			if(isZero(1))
				return children[0];
		}
		
		if(operation == LzBinaryOp.SUB)
		{
			// If subtracting a zero, it doesn't have to exist here.
			if(isZero(1))
				return children[0];
		}
		
		if(operation == LzBinaryOp.MUL)
		{
			// In case of multiplication, if either child is zero, the result will always be zero
			for(int i = 0, len = children.length; i < len; i++)
				if(isZero(i))
					return NumberExpression.ZERO;
			if(isOne(0))
				return children[1];
			if(isOne(1))
				return children[0];
		} else if(operation == LzBinaryOp.DIV)
		{
			if(isZero(0))
				return NumberExpression.ZERO;
			
			if(isOne(1))
				return children[0];
			
			Double v = evalChild(1);
			if(v != null)
			{
				if(LzMath.isZero(v))
					return NumberExpression.ZERO;
				
				if(Double.isFinite(v) && LzMath.isNotZero(v))
				{
					BinaryExpression bin = new BinaryExpression(
							LzBinaryOp.MUL,
							children[0],
							new NumberExpression(1 / v)
					);
					return bin.optimizeStatic(compiler);
				}
			}
		} else if(operation == LzBinaryOp.MOD)
		{
			if(isZero(0) || areEqual())
				return NumberExpression.ZERO;
			
			if(isZero(1))
				return new NumberExpression(Double.NaN);
		}
		
		return this;
	}
	
	private Double evalChild(int child)
	{
		Object staticValue = children[child].evalStatic();
		return staticValue instanceof Number ? ((Number) staticValue).doubleValue() : null;
	}
	
	private boolean isZero(int child)
	{
		Object staticValue = children[child] != null ? children[child].evalStatic() : null;
		return staticValue instanceof Number && LzMath.isZero(((Number) staticValue).doubleValue());
	}
	
	private boolean isNonZero(int child)
	{
		Object staticValue = children[child] != null ? children[child].evalStatic() : null;
		return staticValue instanceof Number && LzMath.isNotZero(((Number) staticValue).doubleValue());
	}
	
	private boolean isOne(int child)
	{
		Object staticValue = children[child] != null ? children[child].evalStatic() : null;
		return staticValue instanceof Number && LzMath.isOne(((Number) staticValue).doubleValue());
	}
	
	private boolean areEqual()
	{
		Object left = children[0] != null ? children[0].evalStatic() : null;
		Object right = children[1] != null ? children[1].evalStatic() : null;
		return left instanceof Number
				&& right instanceof Number
				&& LzMath.isZero(((Number) left).doubleValue() - ((Number) right).doubleValue());
	}
	
	@Override
	public void toLz(MoLangCompiler compiler, LzProgramBuilder builder, ExpressionScope scope)
	{
		ArgType expType = getExpectedLzType();
		if(expType == ArgType.STRING && getOperation() == LzBinaryOp.ADD)
		{
			for(MLExpression child : children)
			{
				if(child != null)
				{
					child.toLz(compiler, builder, scope);
					if(child.getExpectedLzType() != ArgType.STRING)
						builder.addInsn(LzOpcodes.TO_STRING);
				} else
					builder.addConstD(0);
			}
		} else
		{
			for(MLExpression child : children)
			{
				if(child != null)
					child.toLz(compiler, builder, scope);
				else
					builder.addConstD(0);
			}
		}
		builder.addInsn(operation.getOpcode());
	}
	
	@Override
	public ArgType getExpectedLzType()
	{
		// Addition on string + anything acts as concatenation
		if(operation == LzBinaryOp.ADD && ((children[0] != null && children[0].getExpectedLzType() == ArgType.STRING) || (children[1] != null && children[1].getExpectedLzType() == ArgType.STRING)))
			return ArgType.STRING;
		return ArgType.DOUBLE;
	}
}