package org.zeith.hammeranims.molang.visitor;

import lombok.*;
import org.zeith.hammeranims.molang.ExprVisitor;
import org.zeith.hammeranims.molang.Expression;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

@RequiredArgsConstructor
public class FindingVisitor
		implements ExprVisitor
{
	private final Predicate<Expression> predicate;
	
	@Getter
	private final List<Expression> foundExpressions = new ArrayList<>();
	
	@Override
	public Object onVisit(Expression expression)
	{
		if(predicate.test(expression))
		{
			foundExpressions.add(expression);
		}
		
		return null;
	}
}