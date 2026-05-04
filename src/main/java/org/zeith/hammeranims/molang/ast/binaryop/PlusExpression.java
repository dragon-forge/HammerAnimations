package org.zeith.hammeranims.molang.ast.binaryop;

import org.zeith.hammeranims.molang.Expression;
import org.zeith.hammeranims.molang.ast.BinaryOpExpression;
import org.zeith.hammeranims.molang.runtime.MoLangEnvironment;
import org.zeith.hammeranims.molang.runtime.MoScope;
import org.zeith.hammeranims.molang.runtime.value.DoubleValue;
import org.zeith.hammeranims.molang.runtime.value.MoValue;

public class PlusExpression
		extends BinaryOpExpression
{
	
	public PlusExpression(Expression left, Expression right)
	{
		super(left, right);
	}
	
	@Override
	public String getSigil()
	{
		return "+";
	}
	
	@Override
	public MoValue evaluate(MoScope scope, MoLangEnvironment environment)
	{
		return new DoubleValue(
				left.evaluate(scope, environment).asDouble()
						+
						right.evaluate(scope, environment).asDouble()
		);
	}
}
