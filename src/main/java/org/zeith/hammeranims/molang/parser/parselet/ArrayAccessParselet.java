package org.zeith.hammeranims.molang.parser.parselet;

import org.zeith.hammeranims.molang.Expression;
import org.zeith.hammeranims.molang.parser.InfixParselet;
import org.zeith.hammeranims.molang.parser.MoLangParser;
import org.zeith.hammeranims.molang.parser.Precedence;
import org.zeith.hammeranims.molang.ast.ArrayAccessExpression;
import org.zeith.hammeranims.molang.parser.tokenizer.Token;
import org.zeith.hammeranims.molang.parser.tokenizer.TokenType;

public class ArrayAccessParselet
		implements InfixParselet
{
	@Override
	public Expression parse(MoLangParser parser, Token token, Expression leftExpr)
	{
		Expression index = parser.parseExpression(getPrecedence());
		parser.consumeToken(TokenType.ARRAY_RIGHT);
		
		return new ArrayAccessExpression(leftExpr, index);
	}
	
	@Override
	public Precedence getPrecedence()
	{
		return Precedence.ARRAY_ACCESS;
	}
}