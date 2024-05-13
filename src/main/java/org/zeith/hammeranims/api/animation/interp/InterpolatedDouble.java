package org.zeith.hammeranims.api.animation.interp;

import com.google.gson.JsonElement;
import org.zeith.hammeranims.core.js.ExpressionParser;

public interface InterpolatedDouble<T extends IVariableAccess>
{
	double get(T query);
	
	static <T extends IVariableAccess> InterpolatedDouble<T> constant(double d)
	{
		return query -> d;
	}
	
	static <T extends IVariableAccess> InterpolatedDouble<T> parse(String expression)
	{
		return ExpressionParser.parse(expression);
	}
	
	static <T extends IVariableAccess> InterpolatedDouble<T> parse(Object o)
	{
		if(o instanceof Number)
			return constant(((Number) o).doubleValue());
		if(o instanceof String)
			return parse((String) o);
		if(o instanceof JsonElement)
		{
			JsonElement e = (JsonElement) o;
			return parse(e.getAsString());
		}
		return null;
	}
}