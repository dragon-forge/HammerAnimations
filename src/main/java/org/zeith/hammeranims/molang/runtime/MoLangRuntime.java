package org.zeith.hammeranims.molang.runtime;

import lombok.Getter;
import org.zeith.hammeranims.molang.*;
import org.zeith.hammeranims.molang.runtime.struct.ContextStruct;
import org.zeith.hammeranims.molang.runtime.value.*;
import org.zeith.hammeranims.molang.visitor.ExprConnectingVisitor;

import java.util.*;

@Getter
public class MoLangRuntime
{
	protected final MoLangEnvironment environment;
	
	public MoLangRuntime(boolean concurrent)
	{
		environment = new MoLangEnvironment(concurrent);
	}
	
	public MoValue execute(Expression expression)
	{
		return execute(Collections.singletonList(expression));
	}
	
	public MoValue execute(List<Expression> expressions)
	{
		return execute(expressions, Map.of());
	}
	
	public MoValue execute(List<Expression> expressions, Map<String, MoValue> context)
	{
		ExprTraverser traverser = new ExprTraverser();
		traverser.getVisitors().add(new ExprConnectingVisitor());
		traverser.traverse(expressions);
		
		environment.setStruct(MoLangEnvironment.CONTEXT, new ContextStruct(environment, context));
		
		MoValue result = DoubleValue.ZERO;
		MoScope scope = new MoScope();
		for(Expression expression : new ArrayList<>(expressions))
		{
			if(scope.getReturnValue() != null)
			{
				break;
			}
			result = expression.evaluate(scope, environment);
		}
		
		environment.getStructs().get("temp").clear();
		environment.getStructs().remove("context");
		
		return scope.getReturnValue() != null ? scope.getReturnValue() : result;
	}
}