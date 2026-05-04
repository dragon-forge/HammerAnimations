package org.zeith.hammeranims.molang.ast;

import lombok.Value;
import org.zeith.hammeranims.molang.Expression;
import org.zeith.hammeranims.molang.runtime.*;
import org.zeith.hammeranims.molang.runtime.value.MoValue;

import java.util.*;

@Value
public class FuncCallExpression
		implements Expression
{
	Expression name;
	Expression[] args;
	
	@Override
	public MoValue evaluate(MoScope scope, MoLangEnvironment environment)
	{
		List<Expression> params = Arrays.asList(this.args);
		ArrayList<String> names = new ArrayList<>();
		if(this.name instanceof NameExpression)
			names.addAll(((NameExpression) this.name).getNames());
		else
			Collections.addAll(names, this.name.evaluate(scope, environment).asString().split("\\."));
		
		ArrayList<MoValue> paramsParsed = new ArrayList<>(params.size());
		
		for(Expression param : params)
			paramsParsed.add(param.evaluate(scope, environment));
		
		return environment.getValue(names.iterator(), new MoParams(environment, paramsParsed));
	}
}
