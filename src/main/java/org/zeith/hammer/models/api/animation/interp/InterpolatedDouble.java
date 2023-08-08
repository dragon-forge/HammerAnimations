package org.zeith.hammer.models.api.animation.interp;

import org.zeith.hammer.models.core.js.ExpressionParser;

public interface InterpolatedDouble
{
	double get(Query query);
	
	static InterpolatedDouble constant(double d)
	{
		return query -> d;
	}
	
	static InterpolatedDouble parse(String expression)
	{
		return ExpressionParser.parse(expression);
	}
	
	static InterpolatedDouble parse(Object o)
	{
		if(o instanceof Number)
			return constant(((Number) o).doubleValue());
		if(o instanceof String)
			return parse((String) o);
		return null;
	}
}