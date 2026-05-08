package dev.zeith.lzvm.jvm;

import dev.zeith.lzvm.LzVariableStore;

public interface LzFactory
{
	LzExpression instantiate(LzVariableStore store);
	
	static LzExpression[] instantiate(LzVariableStore store, LzFactory... factories)
	{
		if(factories == null) return null;
		if(factories.length == 0) return LzExpression.EMPTY_EXPRESSION;
		LzExpression[] expressions = new LzExpression[factories.length];
		for(int i = 0; i < factories.length; i++) expressions[i] = factories[i].instantiate(store);
		return expressions;
	}
	
	default boolean isGenerated()
	{
		return getClass().isAnnotationPresent(Generated.class);
	}
	
	default String getGeneratedInstructionSet()
	{
		Generated gen = getClass().getDeclaredAnnotation(Generated.class);
		return gen != null ? gen.value() : null;
	}
}