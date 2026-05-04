package org.zeith.hammeranims.molang.ast;

import lombok.Value;
import org.zeith.hammeranims.molang.Expression;
import org.zeith.hammeranims.molang.runtime.*;
import org.zeith.hammeranims.molang.runtime.value.MoValue;

import java.util.*;

@Value
public class ArrayAccessExpression
		implements Expression
{
	Expression array;
	Expression index;
	
	@Override
	public MoValue evaluate(MoScope scope, MoLangEnvironment environment)
	{
		return environment.getValue(getNames(scope, environment));
	}
	
	@Override
	public void assign(MoScope scope, MoLangEnvironment environment, MoValue value)
	{
		environment.setValue(getNames(scope, environment), value);
	}
	
	public Iterator<String> getNames(MoScope scope, MoLangEnvironment environment)
	{
		ArrayList<String> names = new ArrayList<>();
		if(this.array instanceof NameExpression)
			names.addAll(((NameExpression) this.array).getNames());
		else
			Collections.addAll(names, this.array.evaluate(scope, environment).asString().split("\\."));
		names.add(String.valueOf((int) this.index.evaluate(scope, environment).asDouble()));
		return names.iterator();
	}
}
