package dev.zeith.lzvm.molang.parcelets;

import dev.zeith.lzvm.molang.expression.MLExpression;
import dev.zeith.lzvm.molang.parser.*;
import dev.zeith.lzvm.molang.tokenizer.Token;

public interface IInfixParselet
{
	MLExpression parse(MoParser parser, Token token, MLExpression leftExpr);
	
	default EPrecedence getPrecedence()
	{
		return EPrecedence.ANYTHING;
	}
}