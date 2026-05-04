package org.zeith.hammeranims.molang.parser.parselet;

import org.zeith.hammeranims.molang.Expression;
import org.zeith.hammeranims.molang.ast.*;
import org.zeith.hammeranims.molang.parser.*;
import org.zeith.hammeranims.molang.parser.tokenizer.Token;

import java.util.*;

public class NameParselet
		implements PrefixParselet
{
	@Override
	public Expression parse(MoLangParser parser, Token token)
	{
		List<Expression> args = parser.parseArgs();
		String name = parser.fixNameShortcut(token.getText());
		ArrayList<String> names = new ArrayList<>(List.of(name.split("\\.")));
		Expression nameExpr = new NameExpression(names);
		return !args.isEmpty() ? new FuncCallExpression(nameExpr, args.toArray(Expression[]::new)) : nameExpr;
	}
}
