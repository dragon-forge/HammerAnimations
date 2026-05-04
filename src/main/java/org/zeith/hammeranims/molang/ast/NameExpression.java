package org.zeith.hammeranims.molang.ast;

import lombok.Value;
import org.zeith.hammeranims.molang.Expression;
import org.zeith.hammeranims.molang.runtime.*;
import org.zeith.hammeranims.molang.runtime.value.MoValue;

import java.util.ArrayList;

@Value
public class NameExpression
		implements Expression
{
	ArrayList<String> names;
	
	@Override
	public MoValue evaluate(MoScope scope, MoLangEnvironment environment)
	{
		return environment.getValue(names.iterator());
	}
	
	@Override
	public void assign(MoScope scope, MoLangEnvironment environment, MoValue value)
	{
		environment.setValue(names.iterator(), value);
	}
}
