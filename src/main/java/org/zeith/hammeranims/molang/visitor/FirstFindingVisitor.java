package org.zeith.hammeranims.molang.visitor;

import lombok.*;
import org.zeith.hammeranims.molang.ExprTraverser;
import org.zeith.hammeranims.molang.ExprVisitor;
import org.zeith.hammeranims.molang.Expression;

import java.util.function.Predicate;

@RequiredArgsConstructor
public class FirstFindingVisitor
		implements ExprVisitor
{
	private final Predicate<Expression> predicate;
	
	@Getter
	private Expression found;
	
	@Override
	public Object onVisit(Expression expression)
	{
		if(predicate.test(expression))
		{
			found = expression;
			
			return ExprTraverser.ActionType.STOP_TRAVERSAL;
		}
		
		return null;
	}
}