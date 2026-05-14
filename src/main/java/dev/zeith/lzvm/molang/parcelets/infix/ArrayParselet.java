package dev.zeith.lzvm.molang.parcelets.infix;

import dev.zeith.lzvm.exception.LzVMOperationNotSupportedException;
import dev.zeith.lzvm.molang.expression.*;
import dev.zeith.lzvm.molang.parcelets.IInfixParselet;
import dev.zeith.lzvm.molang.parser.*;
import dev.zeith.lzvm.molang.tokenizer.*;

public class ArrayParselet
		implements IInfixParselet
{
	@Override
	public MLExpression parse(MoParser parser, Token token, MLExpression leftExpr)
	{
		if(!(leftExpr instanceof NameExpression))
			throw new LzVMOperationNotSupportedException("Can not parse array access for non-name expression: " + leftExpr.getClass().getSimpleName());
		MLExpression index = parser.parseExpression(EPrecedence.ANYTHING);
		parser.consumeToken(ETokenType.ARRAY_RIGHT);
		return new ArrayExpression((NameExpression) leftExpr, index);
	}
	
	@Override
	public EPrecedence getPrecedence()
	{
		return EPrecedence.ARRAY_ACCESS;
	}
}