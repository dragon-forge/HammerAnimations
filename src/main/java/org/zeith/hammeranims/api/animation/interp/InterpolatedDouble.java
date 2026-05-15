package org.zeith.hammeranims.api.animation.interp;

import com.google.gson.JsonElement;
import dev.zeith.lzvm.jvm.*;
import org.zeith.hammeranims.core.molang.MolangExpressionParser;

public interface InterpolatedDouble
{
	static LzExpression one()
	{
		return constant(1);
	}
	
	static  LzExpression zero()
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
		if(o instanceof JsonElement)
		{
			JsonElement e = (JsonElement) o;
			return parse(e.getAsString());
		}
		return null;
	}
}