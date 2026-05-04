package org.zeith.hammeranims.molang.ast.binaryop;

import org.zeith.hammeranims.molang.Expression;
import org.zeith.hammeranims.molang.ast.BinaryOpExpression;
import org.zeith.hammeranims.molang.runtime.MoLangEnvironment;
import org.zeith.hammeranims.molang.runtime.MoScope;
import org.zeith.hammeranims.molang.runtime.value.DoubleValue;
import org.zeith.hammeranims.molang.runtime.value.MoValue;

import java.util.List;

public class CoalesceExpression
		extends BinaryOpExpression
{
	public CoalesceExpression(Expression left, Expression right)
	{
		super(left, right);
	}
	
	@Override
	public String getSigil()
	{
		return "??";
	}
	
	@Override
	public MoValue evaluate(MoScope scope, MoLangEnvironment environment)
	{
		MoValue evalLeft = this.left.evaluate(scope, environment);
		String leftString = evalLeft.asString();
		List<String> leftNames = List.of(leftString.split("\\."));
		MoValue value = environment.getValue(leftNames.iterator());
		return value != null && !value.equals(DoubleValue.ZERO) ? evalLeft : this.right.evaluate(scope, environment);
	}
}
