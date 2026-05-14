package org.zeith.hammeranims.api.animation.interp;

import dev.zeith.lzvm.jvm.*;
import org.zeith.hammeranims.core.molang.MolangExpressionParser;

import java.util.Objects;

public interface InterpolatedDouble
{
	static LzExpression one()
	{
		return constant(1);
	}
	
	static LzExpression zero()
	{
		return constant(0);
	}
	
	static LzExpression constant(double d)
	{
		return new ConstantExpression(d);
	}
	
	static LzFactory parse(String expression)
	{
		return MolangExpressionParser.parse(expression);
	}
	
	static LzFactory parse(Object o)
	{
		if(o instanceof Number)
			return constant(((Number) o).doubleValue());
		if(o instanceof String)
			return parse((String) o);
		return parse(Objects.toString(o));
	}
}