package dev.zeith.lzvm.molang.parcelets.infix;

import dev.zeith.lzvm.molang.expression.*;
import dev.zeith.lzvm.molang.parcelets.IInfixParselet;
import dev.zeith.lzvm.molang.parser.*;
import dev.zeith.lzvm.molang.tokenizer.*;

public class TernaryParselet
		implements IInfixParselet
{
	@Override
	public MLExpression parse(MoParser parser, Token token, MLExpression leftExpr)
	{
		if(parser.matchToken(ETokenType.COLON))
		{
			// No truth
			return new TernaryExpression(leftExpr, null, parser.parseExpression(getPrecedence()));
		} else
		{
			MLExpression thenExpr = parser.parseExpression(getPrecedence());
			
			// Else branch found
			if(parser.matchToken(ETokenType.COLON))
				return new TernaryExpression(leftExpr, thenExpr, parser.parseExpression(getPrecedence()));
			
			return new TernaryExpression(leftExpr, thenExpr, null);
		}
	}
	
	@Override
	public EPrecedence getPrecedence()
	{
		return EPrecedence.CONDITIONAL;
	}
}